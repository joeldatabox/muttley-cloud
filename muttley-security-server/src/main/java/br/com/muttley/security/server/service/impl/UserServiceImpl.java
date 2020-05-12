package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.exception.throwables.security.MuttleySecurityConflictException;
import br.com.muttley.exception.throwables.security.MuttleySecurityNotFoundException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.model.BasicAggregateResultCount;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.Passwd;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import br.com.muttley.security.server.repository.UserPreferencesRepository;
import br.com.muttley.security.server.repository.UserRepository;
import br.com.muttley.security.server.service.InmutablesPreferencesService;
import br.com.muttley.security.server.service.JwtTokenUtilService;
import br.com.muttley.security.server.service.UserService;
import br.com.muttley.security.server.service.WorkTeamService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static br.com.muttley.model.security.preference.UserPreferences.WORK_TEAM_PREFERENCE;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserPreferencesRepository preferencesRepository;
    private final JwtTokenUtilService tokenUtil;
    private final String tokenHeader;
    private final WorkTeamService workTeamService;
    private final InmutablesPreferencesService inmutablesPreferencesService;
    private final MongoTemplate template;
    private final DocumentNameConfig documentNameConfig;

    @Autowired
    public UserServiceImpl(final UserRepository repository,
                           final UserPreferencesRepository preferencesRepository,
                           @Value("${muttley.security.jwt.controller.tokenHeader}") final String tokenHeader,
                           final JwtTokenUtilService tokenUtil,
                           final WorkTeamService workTeamService,
                           final ObjectProvider<InmutablesPreferencesService> inmutablesPreferencesService,
                           final MongoTemplate template,
                           final DocumentNameConfig documentNameConfig) {
        this.repository = repository;
        this.preferencesRepository = preferencesRepository;
        this.tokenHeader = tokenHeader;
        this.tokenUtil = tokenUtil;
        this.workTeamService = workTeamService;
        this.inmutablesPreferencesService = inmutablesPreferencesService.getIfAvailable();
        this.template = template;
        this.documentNameConfig = documentNameConfig;
    }

    @Override
    public User save(final User user) {
        final User salvedUser = merge(user);
        salvedUser.setPreferences(this.preferencesRepository.save(new UserPreferences().setUser(salvedUser)));
        return salvedUser;
    }

    @Override
    public void save(final User user, final UserPreferences preferences) {
        preferences.setUser(user);
        this.validatePreferences(preferences);
        this.preferencesRepository.save(preferences);
    }

    @Override
    public boolean remove(final User user) {
        final User other = findById(user.getId());
        repository.delete(other);
        return true;
    }

    @Override
    public boolean removeByUserName(final String userName) {
        return this.remove(findByUserName(userName));
    }

    @Override
    public User update(final User user, final JwtToken token) {

        final User otherUser = this.getUserFromToken(token);
        user.setId(otherUser.getId())
                .setEmail(otherUser.getEmail())
                .setUserName(otherUser.getUserName())
                .setNickUsers(otherUser.getNickUsers())
                .setPasswd(otherUser)
                .setLastPasswordResetDate(otherUser.getLastPasswordResetDate())
                .setEnable(otherUser.isEnable())
                .setOdinUser(otherUser.isOdinUser());


        checkNameIsValid(user);
        //validando infos do usuário
        user.validateBasicInfoForLogin();
        return repository.save(user);
    }

    private void checkNameIsValid(final User user) {
        if (user.getName() == null || user.getName().length() < 4) {
            throw new MuttleySecurityBadRequestException(User.class, "nome", "O campo nome deve ter de 4 a 200 caracteres!");
        }
    }

    @Override
    public User updatePasswd(final Passwd passwd) {
        final User user = getUserFromToken(passwd.getToken());
        user.setPasswd(passwd);
        checkNameIsValid(user);
        //validando infos do usuário
        user.validateBasicInfoForLogin();
        return repository.save(user);
    }

    @Override
    public User findByUserName(final String userName) {
        final AggregationResults<User> result = template.aggregate(newAggregation(
                match(
                        new Criteria().orOperator(
                                where("userName").is(userName),
                                where("email").is(userName),
                                where("nickUsers").in(userName)
                        )
                )
        ), User.class, User.class);
        if (result == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        final List<User> users = result.getMappedResults();
        if (CollectionUtils.isEmpty(users)) {
            throw new MuttleySecurityNotFoundException(User.class, "userName", userName + " este registro não foi encontrado");
        }
        if (users.size() > 1) {
            throw new MuttleyException("Erro interno no sistema");
        }

        return users.get(0);
    }

    @Override
    public User findUserByEmailOrUserNameOrNickUsers(final String email, final String userName, final Set<String> nickUsers) {
        final Set<String> nicks = createSetForNicks(email, userName, nickUsers);
        final AggregationResults<User> result = template.aggregate(newAggregation(
                match(
                        new Criteria().orOperator(
                                where("userName").in(nicks),
                                where("email").in(nicks),
                                where("nickUsers").in(nicks)
                        )
                )
        ), User.class, User.class);
        if (result == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        final List<User> users = result.getMappedResults();
        if (CollectionUtils.isEmpty(users)) {
            throw new MuttleySecurityNotFoundException(User.class, null, userName + " este registro não foi encontrado");
        }
        if (users.size() > 1) {
            throw new MuttleyException("Mais de um usuário foi encontrado");
        }
        return users.get(0);
    }

    @Override
    public boolean existUserByEmailOrUserNameOrNickUsers(final String email, final String userName, final Set<String> nickUsers) {
        final Set<String> nicks = createSetForNicks(email, userName, nickUsers);
        final AggregationResults<UserViewServiceImpl.ResultCount> result = template.aggregate(newAggregation(
                match(
                        new Criteria().orOperator(
                                where("userName").in(nicks),
                                where("email").in(nicks),
                                where("nickUsers").in(nicks)
                        )
                ), Aggregation.count().as("count")
        ), User.class, UserViewServiceImpl.ResultCount.class);
        if (result == null) {
            return false;
        }

        final UserViewServiceImpl.ResultCount resultCount = result.getUniqueMappedResult();
        if (resultCount == null) {
            return false;
        }
        return resultCount.getCount() > 0;
    }

    private Set<String> createSetForNicks(final String email, final String userName, final Set<String> nickUsers) {
        final Set<String> nicks = new HashSet<>();
        if (!StringUtils.isEmpty(email)) {
            nicks.add(email);
        }
        if (!StringUtils.isEmpty(userName)) {
            nicks.add(userName);
        }
        if (!CollectionUtils.isEmpty(nickUsers)) {
            nickUsers.stream().filter(it -> !StringUtils.isEmpty(it)).forEach(it -> nicks.add(it));
        }
        return nicks;
    }

    public User findByEmail(final String email) {
        final User user = repository.findByEmail(email);
        if (user == null) {
            throw new MuttleySecurityNotFoundException(User.class, "email", email + " este registro não foi encontrado");
        }
        return user;
    }

    @Override
    public User findById(final String id) {
        final User user = repository.findOne(id);
        if (user == null) {
            throw new MuttleySecurityNotFoundException(User.class, "id", id + " este registro não foi encontrado");
        }
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return (Collection<User>) repository.findAll();
    }

    @Override
    public User getUserFromToken(final JwtToken token) {
        if (token != null && !token.isEmpty()) {
            final String userName = this.tokenUtil.getUsernameFromToken(token.getToken());
            if (!isNullOrEmpty(userName)) {
                final User user = findByUserName(userName);
                final UserPreferences preferences = this.preferencesRepository.findByUser(user);
                user.setPreferences(preferences);
                if (preferences.contains(WORK_TEAM_PREFERENCE)) {
                    user.setCurrentWorkTeam(this.workTeamService.findById(user, preferences.get(WORK_TEAM_PREFERENCE).getValue().toString()));
                }
                return user;
            }
        }
        throw new MuttleySecurityUnauthorizedException();
    }

    @Override
    public UserPreferences loadPreference(final User user) {
        return this.preferencesRepository.findByUser(user);
    }

    @Override
    public List<User> getUsersFromPreference(final Preference preference) {
        /**
         * db.getCollection("muttley-users-preferences").aggregate([
         *                 {$match:{preferences:{$elemMatch: {"key":"UserColaborador", value:"5e28bd0d6f985c00017e7bb6"}}}},
         *                 {$project: {user:1}}
         * ])
         */
        final AggregationResults<UserPreferences> results = this.template.aggregate(
                newAggregation(
                        match(where("preferences").elemMatch(where("key").is(preference.getKey()).and("value").is(preference.getValue()))),
                        project("user")
                ),
                UserPreferences.class,
                UserPreferences.class
        );
        if (results != null && !CollectionUtils.isEmpty(results.getMappedResults())) {
            return results.getMappedResults()
                    .stream()
                    .map(UserPreferences::getUser)
                    .collect(toList());
        }
        throw new MuttleyNoContentException(User.class, null, "Nenhum usuário encontrado");
    }

    @Override
    public User getUserFromPreference(final Preference preference) {
        /**
         * db.getCollection("muttley-users-preferences").aggregate([
         *                 {$match:{preferences:{$elemMatch: {"key":"UserColaborador", value:"5e28bd0d6f985c00017e7bb6"}}}},
         *                 {$project: {user:1}},
         *                 {$limit:1}
         * ])
         */
        final AggregationResults<UserPreferences> results = this.template.aggregate(
                newAggregation(
                        match(where("preferences").elemMatch(where("key").is(preference.getKey()).and("value").is(preference.getValue()))),
                        project("user"),
                        limit(1)
                ),
                UserPreferences.class,
                UserPreferences.class
        );
        if (results != null && results.getUniqueMappedResult() != null) {
            return results.getUniqueMappedResult().getUser();
        }
        throw new MuttleyNotFoundException(User.class, null, "Nenhum usuário encontrado");
    }

    @Override
    public boolean constainsPreference(final User user, final String keyPreference) {
        /**
         * db.getCollection("muttley-users-preferences").aggregate([
         *     {$match:{"user.$id":ObjectId("5e84ed76e684d90007e94718")}},
         *     {$project:{preferences:1}},
         *     {$unwind:"$preferences"},
         *     {$match:{"preferences.key":"UserColaborador"}},
         *     {$count:"result"}
         * ])
         */

        if (StringUtils.isEmpty(keyPreference)) {
            return false;
        }

        final AggregationResults<BasicAggregateResultCount> result = this.template.aggregate(
                newAggregation(
                        match(where("user.$id").is(new ObjectId(user.getId()))),
                        project("preferences"),
                        unwind("$preferences"),
                        match(where("preferences.key").is(keyPreference)),
                        Aggregation.count().as("resul")
                ),
                UserPreferences.class,
                BasicAggregateResultCount.class
        );
        if (result == null || result.getUniqueMappedResult() == null) {
            return false;
        }
        return result.getUniqueMappedResult().getResult() > 0;
    }

    private User merge(final User user) {
        checkNameIsValid(user);

        //validando infos do usuário
        user.validateBasicInfoForLogin();

        //se não preencheu nada bloqueamos
        if (StringUtils.isEmpty(user.getUserName()) && StringUtils.isEmpty(user.getEmail()) && CollectionUtils.isEmpty(user.getNickUsers())) {
            throw new MuttleySecurityBadRequestException(User.class, null, "Informe o email, userName ou os nickUsers");
        }

        //caso não tenha se inform um userName, vamos gerar um randomicamente
        if (StringUtils.isEmpty(user.getUserName())) {
            user.setUserName(user.getName() + new ObjectId(new Date()).toString());
        }

        if (user.getId() == null) {

            //validando se já existe esse usuário no sistema
            //devemos garantir que qualquer info de email, userName e ou nickUsers sejam unicos
            /*checkIsUniqueUserName(user);
            checkIsUniqueEmail(user);
            checkIsUniqueNickUsers(user);*/
            this.checkIndexUser(user);

            //validando se preencheu a senha corretamente
            if (!user.isValidPasswd()) {
                throw new MuttleySecurityBadRequestException(User.class, "passwd", "Informe uma senha válida!");
            }
            return repository.save(user);
        } else {
            final User self = findById(user.getId());

            /*checkIsUniqueUserName(user);
            checkIsUniqueEmail(user);
            checkIsUniqueNickUsers(user);*/
            this.checkIndexUser(user);

/*
            if (!self.getUserName().equals(user.getUserName())) {
                throw new MuttleySecurityBadRequestException(User.class, "userName", "O userName não pode ser modificado!").setStatus(HttpStatus.NOT_ACCEPTABLE);
            }
*/

            //garantindo que a senha não irá ser modificada
            user.setPasswd(self);

            return repository.save(user);
        }
    }

    private void checkIndexUser(final User user) {
        final Set<String> userNames = new HashSet<>(user.getNickUsers());
        if (!StringUtils.isEmpty(user.getEmail())) {
            userNames.add(user.getEmail());
        }
        if (!StringUtils.isEmpty(user.getUserName())) {
            userNames.add(user.getUserName());
        }

        final Criteria basicCriteria = new Criteria().orOperator(
                where("userName").in(userNames),
                where("email").in(userNames),
                where("nickUsers").in(userNames)
        );

        Aggregation aggregation = null;

        if (StringUtils.isEmpty(user.getId())) {
            aggregation = newAggregation(
                    match(basicCriteria),
                    Aggregation.count().as("count")
            );
        } else {
            aggregation = newAggregation(
                    match(basicCriteria.and("_id").ne(new ObjectId(user.getId()))),
                    Aggregation.count().as("count")
            );
        }

        final AggregationResults<UserViewServiceImpl.ResultCount> result = template.aggregate(aggregation, User.class, UserViewServiceImpl.ResultCount.class);

        if (result != null) {
            final UserViewServiceImpl.ResultCount resultCount = result.getUniqueMappedResult();
            if (resultCount != null && resultCount.getCount() > 0) {
                final MuttleySecurityConflictException ex = new MuttleySecurityConflictException(User.class, null, "Por favor, busque utilizar outra opção!");
                //para devolver uma resposta adequada, vamos verificar quais nomes estão indisponíveis;
                ex.addDetails("indisponivel", userNames
                        .stream()
                        .filter(u -> this.existUserName(user.getId(), u))
                        .collect(toList()));
                throw ex;
            }
        }

    }

    /**
     * Metodo usado para verificar se um determinado nome esta disponivel ou não
     */
    private boolean existUserName(final String id, final String userName) {
        if (!StringUtils.isEmpty(userName)) {
            final AggregationResults<UserViewServiceImpl.ResultCount> result;
            if (StringUtils.isEmpty(id)) {
                result = template.aggregate(newAggregation(
                        match(
                                new Criteria().orOperator(
                                        where("userName").in(userName),
                                        where("email").in(userName),
                                        where("nickUsers").in(userName)
                                )
                        ),
                        Aggregation.count().as("count")
                ), User.class, UserViewServiceImpl.ResultCount.class);
            } else {
                result = template.aggregate(newAggregation(
                        match(
                                new Criteria().orOperator(
                                        where("userName").in(userName),
                                        where("email").in(userName),
                                        where("nickUsers").in(userName)
                                ).and("_id").ne(new ObjectId(id))
                        ),
                        Aggregation.count().as("count")
                ), User.class, UserViewServiceImpl.ResultCount.class);
            }
            if (result != null) {
                final UserViewServiceImpl.ResultCount resultCount = result.getUniqueMappedResult();
                return resultCount != null && resultCount.getCount() > 0;
            }
        }
        return false;
    }

    public Long count(final User user, final Map<String, Object> allRequestParams) {
        //return repository.count(allRequestParams);
        throw new UnsupportedOperationException("Implemente o methodo");
    }


    public List<User> findAll(final User user, final Map<String, Object> allRequestParams) {
        throw new UnsupportedOperationException("Implemente o methodo");
        //return repository.findAll(allRequestParams);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return new JwtUser(this.findByUserName(username));
    }

    /**
     * Retorna true caso encontre algum nickUser com idUser diferente do informado
     */
    private boolean existNickUsers(final ObjectId idUser, final String nickUsers) {
        final AggregationResults<UserViewServiceImpl.ResultCount> result = template.aggregate(newAggregation(
                match(where("_id").ne(idUser).and("nickUsers").in(nickUsers)),
                Aggregation.count().as("count")
        ), User.class, UserViewServiceImpl.ResultCount.class);

        if (result == null) {
            return false;
        }
        return result.getUniqueMappedResult().getCount() > 0;
    }

    /**
     * Retorna true caso encontre algum nickUser
     */
    private boolean existNickUsers(final String nickUsers) {


        final AggregationResults<UserViewServiceImpl.ResultCount> result = template.aggregate(newAggregation(
                match(where("nickUsers").in(nickUsers)),
                Aggregation.count().as("count")
        ), User.class, UserViewServiceImpl.ResultCount.class);

        if (result == null) {
            return false;
        }
        return result.getUniqueMappedResult().getCount() > 0;
    }

    private void validatePreferences(final UserPreferences preferences) {
        //se o serviço foi injetado, devemos validar
        //se a preferencia do usuário já tiver o id, devemo validar
        if (this.inmutablesPreferencesService != null && !StringUtils.isEmpty(preferences.getId())) {
            final Set<String> inmutableKeys = this.inmutablesPreferencesService.getInmutablesKeysPreferences();
            if (!CollectionUtils.isEmpty(inmutableKeys)) {
                //recuperando as preferencias sem alterações
                final UserPreferences otherPreferences = this.preferencesRepository.findByUser(preferences.getUser());
                if (otherPreferences != null) {
                    //percorrendo todas a keys que nsão proibidas as alterações
                    inmutableKeys.forEach(inmutableKey -> {
                        if (!StringUtils.isEmpty(inmutableKey)) {
                            //se as preferencias existir no banco
                            if (otherPreferences.contains(inmutableKey)) {
                                //recuperando a preferencia do objeto atual
                                final Preference pre = preferences.get(inmutableKey);
                                //se a preferencial atual for null ou tiver sido modificada
                                if (pre == null || !pre.getValue().equals(otherPreferences.get(inmutableKey).getValue())) {
                                    throw new MuttleyBadRequestException(Preference.class, "key", "Não é possível fazer a alteração da preferencia [" + inmutableKey + ']')
                                            .addDetails("key", inmutableKey)
                                            .addDetails("currentValue", otherPreferences.get(inmutableKey).getValue());
                                }
                            }
                        }
                    });
                }
            }
        }
    }
}
