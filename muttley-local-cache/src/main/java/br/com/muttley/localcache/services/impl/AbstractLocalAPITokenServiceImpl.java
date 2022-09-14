package br.com.muttley.localcache.services.impl;

import br.com.muttley.localcache.services.LocalAPITokenService;
import br.com.muttley.model.security.APIToken;
import br.com.muttley.redis.service.RedisService;

public abstract class AbstractLocalAPITokenServiceImpl implements LocalAPITokenService {
    protected final RedisService redisService;
    private static final long DEFAULT_EXPIRATION = 60 * 60 * 24;//24 horas em segundos

    protected AbstractLocalAPITokenServiceImpl(RedisService redisService) {
        this.redisService = redisService;
    }

    protected LocalAPITokenService saveInCache(final APIToken apiToken) {
        this.redisService.set(this.getBasicKey(apiToken.getToken()), apiToken, DEFAULT_EXPIRATION);
        return this;
    }

    public APIToken loadAPIToken(final String token) {
        return (APIToken) this.redisService.get(this.getBasicKey(token));
    }

    @Override
    public LocalAPITokenService expireAPIToken(String token) {
        this.redisService.delete(this.getBasicKey(token));
        return this;
    }

    protected String getBasicKey(final String token) {
        return LocalAPITokenService.BASIC_KEY + token;
    }
}
