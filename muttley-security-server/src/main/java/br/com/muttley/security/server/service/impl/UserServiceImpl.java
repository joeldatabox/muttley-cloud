package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.exception.throwables.security.MuttleySecurityConflictException;
import br.com.muttley.exception.throwables.security.MuttleySecurityNotFoundException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.Passwd;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.UserCreatedEvent;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.repository.UserRepository;
import br.com.muttley.security.server.service.InmutablesPreferencesService;
import br.com.muttley.security.server.service.UserPreferenceService;
import br.com.muttley.security.server.service.UserService;
import br.com.muttley.security.server.service.WorkTeamService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
import java.util.Optional;
import java.util.Set;

import static br.com.muttley.model.security.preference.UserPreferences.WORK_TEAM_PREFERENCE;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
@Service
public class UserServiceImpl implements UserService {

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository repository;
    private final UserPreferenceService userPreferenceService;
    private final JwtTokenUtilService tokenUtil;
    private final WorkTeamService workTeamService;
    private final InmutablesPreferencesService inmutablesPreferencesService;
    private final MongoTemplate template;

    @Autowired
    public UserServiceImpl(final ApplicationEventPublisher eventPublisher,
                           final UserRepository repository,
                           final UserPreferenceService userPreferenceService,
                           final JwtTokenUtilService tokenUtil,
                           final WorkTeamService workTeamService,
                           final ObjectProvider<InmutablesPreferencesService> inmutablesPreferencesService,
                           final MongoTemplate template) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
        this.userPreferenceService = userPreferenceService;
        this.tokenUtil = tokenUtil;
        this.workTeamService = workTeamService;
        this.inmutablesPreferencesService = inmutablesPreferencesService.getIfAvailable();
        this.template = template;
    }

    @Override
    public User save(final User user) {
        final User salvedUser = merge(user);
        UserPreferences preferences;
        try {
            preferences = userPreferenceService.getPreferences(user);
        } catch (MuttleyNotFoundException ex) {
            preferences = new UserPreferences();
        }
        salvedUser.setPreferences(this.userPreferenceService.save(salvedUser, preferences.setUser(salvedUser)));
        eventPublisher.publishEvent(new UserCreatedEvent(user));
        return salvedUser;
    }

    @Override
    public void save(final User user, final UserPreferences preferences) {
        preferences.setUser(user);
        this.userPreferenceService.save(user, preferences);
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
    public User update(final User user) {
        return merge(user);
    }

    @Override
    public User updatePasswd(final Passwd passwd) {
        final User user = getUserFromToken(passwd.getToken());
        user.setPasswd(passwd);
        return save(user);
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
        final Optional<User> user = repository.findById(id);
        if (!user.isPresent()) {
            throw new MuttleySecurityNotFoundException(User.class, "id", id + " este registro não foi encontrado");
        }
        return user.get();
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
                final UserPreferences preferences = this.userPreferenceService.getPreferences(user);
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
        return this.userPreferenceService.getPreferences(user);
    }

    private User merge(final User user) {
        if (user.getName() == null || user.getName().length() < 4) {
            throw new MuttleySecurityBadRequestException(User.class, "nome", "O campo nome deve ter de 4 a 200 caracteres!");
        }

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


}
