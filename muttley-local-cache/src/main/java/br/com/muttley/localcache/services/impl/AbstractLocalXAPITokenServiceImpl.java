package br.com.muttley.localcache.services.impl;

import br.com.muttley.localcache.services.LocalXAPITokenService;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.XAPIToken;
import br.com.muttley.redis.service.RedisService;

public abstract class AbstractLocalXAPITokenServiceImpl implements LocalXAPITokenService {
    protected final RedisService redisService;
    private static final long DEFAULT_EXPIRATION = 60 * 60 * 24;//24 horas em segundos

    protected AbstractLocalXAPITokenServiceImpl(RedisService redisService) {
        this.redisService = redisService;
    }

    protected LocalXAPITokenService saveInCache(final XAPIToken XAPIToken) {
        this.redisService.set(this.getBasicKey(XAPIToken.getToken(), Type.XAPIToken), XAPIToken, DEFAULT_EXPIRATION);
        return this;
    }

    protected LocalXAPITokenService saveInCache(final XAPIToken xapiToken, final JwtToken jwtToken) {
        return this.saveInCache(xapiToken.getToken(), jwtToken);
    }

    protected LocalXAPITokenService saveInCache(final String xapiToken, final JwtToken jwtToken) {
        this.redisService.set(this.getBasicKey(xapiToken, Type.JWTToken), jwtToken, DEFAULT_EXPIRATION);
        return this;
    }

    public XAPIToken loadAPIToken(final String token) {
        return (XAPIToken) this.redisService.get(this.getBasicKey(token, Type.XAPIToken));
    }

    @Override
    public JwtToken loadJwtTokenFrom(String xAPIToken) {
        return (JwtToken) this.redisService.get(this.getBasicKey(xAPIToken, Type.JWTToken));
    }

    @Override
    public LocalXAPITokenService expireAPIToken(String token) {
        this.redisService.delete(this.getBasicKey(token, Type.XAPIToken));
        return this;
    }

    protected String getBasicKey(final String token, Type type) {
        return LocalXAPITokenService.BASIC_KEY + type.name() + ":" + token;
    }
}
