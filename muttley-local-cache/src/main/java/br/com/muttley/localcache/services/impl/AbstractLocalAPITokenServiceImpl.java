package br.com.muttley.localcache.services.impl;

import br.com.muttley.localcache.services.LocalAPITokenService;
import br.com.muttley.model.security.XAPIToken;
import br.com.muttley.redis.service.RedisService;

public abstract class AbstractLocalAPITokenServiceImpl implements LocalAPITokenService {
    protected final RedisService redisService;
    private static final long DEFAULT_EXPIRATION = 60 * 60 * 24;//24 horas em segundos

    protected AbstractLocalAPITokenServiceImpl(RedisService redisService) {
        this.redisService = redisService;
    }

    protected LocalAPITokenService saveInCache(final XAPIToken XAPIToken) {
        this.redisService.set(this.getBasicKey(XAPIToken.getToken()), XAPIToken, DEFAULT_EXPIRATION);
        return this;
    }

    public XAPIToken loadAPIToken(final String token) {
        return (XAPIToken) this.redisService.get(this.getBasicKey(token));
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
