package br.com.muttley.security.server.service.impl;

import br.com.muttley.domain.impl.ServiceImpl;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.metadata.headers.HeaderAuthorizationJWT;
import br.com.muttley.model.autoconfig.DocumentNameConfig;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.UserResolverEvent;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.mongo.result.UniqueResult;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.server.repository.UserPreferencesRepository;
import br.com.muttley.security.server.service.InmutablesPreferencesService;
import br.com.muttley.security.server.service.UserPreferenceService;
import org.bson.Document;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Arrays.asList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 01/11/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Service
public class UserPreferenceServiceImpl extends ServiceImpl<UserPreferences> implements UserPreferenceService {
    private final MongoTemplate template;
    private final UserPreferencesRepository repository;
    private final HeaderAuthorizationJWT headerAuthorizationJWT;
    private final RedisService redisService;
    private final ApplicationEventPublisher eventPublisher;
    private final DocumentNameConfig documentNameConfig;
    private final InmutablesPreferencesService inmutablesPreferencesService;
    @Value("${muttley.security.jwt.token.expiration:3600000}")
    private Integer expirationToken;
    private static final String KEY = "preferences";

    @Autowired
    public UserPreferenceServiceImpl(
            final MongoTemplate template,
            final UserPreferencesRepository repository,
            final HeaderAuthorizationJWT headerAuthorizationJWT,
            final RedisService redisService,
            final ApplicationEventPublisher eventPublisher,
            final DocumentNameConfig documentNameConfig,
            final ObjectProvider<InmutablesPreferencesService> inmutablesPreferencesService) {
        super(repository, UserPreferences.class);
        this.template = template;
        this.repository = repository;
        this.headerAuthorizationJWT = headerAuthorizationJWT;
        this.redisService = redisService;
        this.eventPublisher = eventPublisher;
        this.documentNameConfig = documentNameConfig;
        this.inmutablesPreferencesService = inmutablesPreferencesService.getIfAvailable();
    }

    @Override
    public UserPreferences save(User user, UserPreferences userPreferences) {
        this.validatePreferences(userPreferences);
        final UserPreferences salvedPreferences = this.repository.save(userPreferences.setUser(user));
        updatePreferencesCache(user, salvedPreferences);
        return salvedPreferences;
    }

    @Override
    public UserPreferences getPreferences(User user) {
        final UserPreferences preferences = this.repository.findByUser(user);
        if (preferences == null) {
            throw new MuttleyNotFoundException(UserPreferences.class, "user", "Nenhuma preferencia encontrada");
        }
        return preferences;
    }

    @Override
    public UserPreferences getPreferences(final JwtToken token) {
        return getPreferences(getUserFromToken(token));
    }

    @Override
    public void setPreferences(User user, Preference preference) {
        if (!preference.isValid()) {
            throw new MuttleyBadRequestException(Preference.class, "key", "valor inválido");
        }
        final UserPreferences preferences = this.repository.save(getPreferences(user).set(preference));
        updatePreferencesCache(user, preferences);
    }

    @Override
    public void setPreferences(String email, Preference preferences) {
        setPreferences(loadUserByEmail(email), preferences);
    }

    @Override
    public void setPreferences(JwtToken token, Preference preference) {
        this.setPreferences(getUserFromToken(token), preference);
    }

    @Override
    public void removePreference(User user, String key) {
        final UserPreferences preferences = this.repository.save(getPreferences(user).remove(key));
        updatePreferencesCache(user, preferences);
    }

    @Override
    public void removePreference(JwtToken token, String key) {
        this.removePreference(getUserFromToken(token), key);
    }

    private User getUserFromToken(final JwtToken token) {
        if (token.isEmpty()) {
            throw new MuttleyBadRequestException(null, null, "informe um token válido");
        }
        final String emailFromToken = token.getUsername();

        if (isNullOrEmpty(emailFromToken)) {
            throw new MuttleyBadRequestException(null, null, "informe um token válido");
        }

        //carregando o usuário
        final UserResolverEvent userResolverEvent = new UserResolverEvent(emailFromToken);
        this.eventPublisher.publishEvent(userResolverEvent);
        return userResolverEvent.getUserResolved();
    }

    /**
     * Irá salvar ou atualizar as preferencias do usuário no cache, caso seja necessário
     */
    private void updatePreferencesCache(final User user, final UserPreferences userPreferences) {
        updatePreferencesCache(user.getUserName(), userPreferences);
    }

    private void updatePreferencesCache(final String email, final UserPreferences userPreferences) {
        this.validatePreferences(userPreferences);
        final String keyCache = createKey(email);
        if (redisService.hasKey(keyCache)) {
            redisService.set(keyCache, userPreferences, redisService.getExpire(keyCache));
        }
    }

    private String createKey(final String email) {
        return KEY + ":" + email;
    }

    private User loadUserByEmail(final String email) {
        /**
         * db.getCollection("muttley-users-preferences").aggregate([
         *     {$project: {"user":{$objectToArray: "$user"}}},
         *     {$project:{"user":{$arrayElemAt:["$user.v",1]}}},
         *     {$lookup:{
         *         from:"muttley-users",
         *         localField: "user",
         *         foreignField: "_id",
         *         as:"user"
         *     }},
         *     {$unwind: "$user"},
         *     {$match: {"user.email": EMAIL_DESEJADO}},
         *     {$project: {"userId":"$user._id"}}
         *     ])
         */
        final AggregationResults<UniqueResult> results = this.template
                .aggregate(
                        newAggregation(
                                //transformando o objeto em array
                                project().and(context -> new Document("$objectToArray", "$user")).as("user"),
                                //pegando somento o objectid para fazer o processo de lookup
                                project().and(context -> new Document("$arrayElemAt", asList("$user.v", 1))).as("user"),
                                //fazendo join
                                lookup(documentNameConfig.getNameCollectionUser(), "user", "_id", "user"),
                                unwind("$user"),

                                //filtrando
                                match(where("user.email").is(email)),
                                //pegando somente o que é necessário
                                project().and("user._id").as("result")
                        ), this.documentNameConfig.getNameCollectionUserPreferences(),
                        UniqueResult.class
                );
        return new User().setId(results.getUniqueMappedResult().getResult().toString());
    }

    private void validatePreferences(final UserPreferences preferences) {
        //se o serviço foi injetado, devemos validar
        //se a preferencia do usuário já tiver o id, devemo validar
        if (this.inmutablesPreferencesService != null && !StringUtils.isEmpty(preferences.getId())) {
            final Set<String> inmutableKeys = this.inmutablesPreferencesService.getInmutablesKeysPreferences();
            if (!CollectionUtils.isEmpty(inmutableKeys)) {
                //recuperando as preferencias sem alterações
                final UserPreferences otherPreferences = this.repository.findByUser(preferences.getUser());
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
