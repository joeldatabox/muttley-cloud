package br.com.muttley.security.infra.services.impl;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.infra.services.CacheUserPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira on 02/11/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Service
public class CacheUserPreferencesImpl implements CacheUserPreferences {
    private final RedisService redisService;
    private static final String KEY = "preferences";

    @Autowired
    public CacheUserPreferencesImpl(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public UserPreferences get(User user) {
        return (UserPreferences) redisService.get(createKey(user));
    }

    @Override
    public CacheUserPreferences set(User user, UserPreferences userPreferences, final long time) {
        redisService.set(createKey(user), userPreferences, time);
        return this;
    }

    private String createKey(final String email) {
        return KEY + "-" + email;
    }

    private String createKey(final User user) {
        return createKey(user.getEmail());
    }
}
