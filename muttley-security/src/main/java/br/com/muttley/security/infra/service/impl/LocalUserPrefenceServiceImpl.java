package br.com.muttley.security.infra.service.impl;

import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.UserPreferenceServiceClient;
import br.com.muttley.security.infra.service.LocalUserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira 24/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class LocalUserPrefenceServiceImpl implements LocalUserPreferenceService {
    private final RedisService redisService;
    private final UserPreferenceServiceClient userPreferenceService;

    @Autowired
    public LocalUserPrefenceServiceImpl(final RedisService redisService, final UserPreferenceServiceClient userPreferenceService) {
        this.redisService = redisService;
        this.userPreferenceService = userPreferenceService;
    }

    @Override
    public UserPreferences getUserPreferences(final JwtUser jwtUser, final User user) {
        //verificando se já existe a chave
        if(this.redisService.hasKey(this.))
        return null;
    }

    @Override
    public void refreshUserPreferencesFor(final User user) {

    }

    private String getBasicKey(final User user) {
        return BASIC_KEY + user.getId();
    }
}
