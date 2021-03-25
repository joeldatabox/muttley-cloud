package br.com.muttley.security.infra.service.impl;

import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.UserPreferencesResolverEvent;
import br.com.muttley.model.security.events.UserPreferencesResolverEvent.UserPreferencesResolverEventItem;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.OwnerServiceClient;
import br.com.muttley.security.feign.UserPreferenceServiceClient;
import br.com.muttley.security.infra.service.LocalUserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Joel Rodrigues Moreira 24/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class LocalUserPrefenceServiceImpl implements LocalUserPreferenceService {
    private final RedisService redisService;
    private final UserPreferenceServiceClient userPreferenceService;
    private final OwnerServiceClient ownerService;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public LocalUserPrefenceServiceImpl(final RedisService redisService, final UserPreferenceServiceClient userPreferenceService, OwnerServiceClient ownerService, ApplicationEventPublisher publisher) {
        this.redisService = redisService;
        this.userPreferenceService = userPreferenceService;
        this.ownerService = ownerService;
        this.publisher = publisher;
    }

    @Override
    public UserPreferences getUserPreferences(final JwtToken jwtUser, final User user) {
        if (jwtUser.getUsername().equals(user.getUserName())) {
            final UserPreferences preferences;
            //verificando se já existe a chave
            if (this.redisService.hasKey(this.getBasicKey(user))) {
                //recuperando as preferencias do usuário do cache
                preferences = this.getPreferenceInCache(jwtUser, user);
            } else {
                //se chegou aqui é sinal que ainda não exite um preferencia carregada em cache, logo devemo buscar
                //recuperando as preferencias do servidor de segurança
                preferences = userPreferenceService.getUserPreferences();
                //salvando no cache para evitar loops desnecessários
                this.savePreferenceInCache(jwtUser, user, preferences);
                if (preferences != null) {
                    //por comodidade vamo disparar o evento para resolução dos itens das preferencias
                    final UserPreferencesResolverEvent event = new UserPreferencesResolverEvent(new UserPreferencesResolverEventItem(user, preferences));
                    this.publisher.publishEvent(event);
                    //salvando no cache novamente para guardar as modificações
                    this.savePreferenceInCache(jwtUser, user, preferences);
                }
            }
            return preferences;
        }
        throw new MuttleySecurityBadRequestException(UserPreferences.class, "user", "O usuário informado é diferente do presente no token");
    }

    @Override
    public void expireUserPreferences(User user) {
        //deletando item do cache
        this.redisService.delete(this.getBasicKey(user));
    }

    private String getBasicKey(final User user) {
        return BASIC_KEY + user.getId();
    }

    private void savePreferenceInCache(final JwtToken token, final User user, final UserPreferences userPreferences) {
        final Map<String, Object> userPreferencesMap = new HashMap<>();
        userPreferencesMap.put("id", userPreferences.getId());
        if (!userPreferences.isEmpty()) {
            userPreferencesMap.put("preferences",
                    userPreferences.getPreferences()
                            .parallelStream()
                            .map(pre -> {
                                final Map<String, Object> preferencesItemMap = new HashMap<>();
                                preferencesItemMap.put("key", pre.getKey());
                                preferencesItemMap.put("value", pre.getValue());
                                preferencesItemMap.put("resolved", pre.getResolved());
                                return preferencesItemMap;
                            }).collect(Collectors.toList())
            );
        }
        this.redisService.set(this.getBasicKey(user), userPreferencesMap, token.getExpiration());
    }

    private UserPreferences getPreferenceInCache(final JwtToken token, final User user) {
        final Map<String, Object> userPreferencesMap = (Map<String, Object>) this.redisService.get(this.getBasicKey(user));
        final UserPreferences preferences = new UserPreferences();
        preferences.setId(String.valueOf(userPreferencesMap.get("id")));
        if (userPreferencesMap.containsKey("preferences")) {
            preferences.setPreferences(
                    ((List<Map<String, Object>>) userPreferencesMap.get("preferences"))
                            .parallelStream()
                            .map(mapIt -> {
                                final Object valeu = mapIt.get("value") == null ? null : mapIt.get("value");
                                final Preference preference = new Preference(
                                        String.valueOf(mapIt.get("key")),
                                        mapIt.get("value")
                                );
                                return preference.setResolved(mapIt.get("resolved"));
                            }).collect(Collectors.toSet())
            );
        }
        return preferences;
    }
}
