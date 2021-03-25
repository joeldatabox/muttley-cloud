package br.com.muttley.localcache.services.impl;

import br.com.muttley.localcache.services.LocalUserPreferenceService;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Joel Rodrigues Moreira 24/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractLocalUserPrefenceServiceImpl implements LocalUserPreferenceService {
    protected final RedisService redisService;
    protected final ApplicationEventPublisher publisher;

    @Autowired
    public AbstractLocalUserPrefenceServiceImpl(final RedisService redisService, ApplicationEventPublisher publisher) {
        this.redisService = redisService;
        this.publisher = publisher;
    }

    @Override
    public UserPreferences getUserPreferences(final JwtToken jwtUser, final User user) {
        throw new NotImplementedException();
    }

    @Override
    public void expireUserPreferences(User user) {
        //deletando item do cache
        this.redisService.delete(this.getBasicKey(user));
    }

    protected String getBasicKey(final User user) {
        return BASIC_KEY + user.getId();
    }

    protected void savePreferenceInCache(final JwtToken token, final User user, final UserPreferences userPreferences) {
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

    protected UserPreferences getPreferenceInCache(final JwtToken token, final User user) {
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
