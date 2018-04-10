package br.com.muttley.security.infra.service.impl;

import br.com.muttley.model.security.jwt.JwtUser;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.infra.service.CacheUserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Joel Rodrigues Moreira on 09/01/18.
 * @project demo
 */
public class CacheUserAuthenticationServiceImpl implements CacheUserAuthenticationService {

    private final RedisService redisService;
    private final int expiration;

    @Autowired
    public CacheUserAuthenticationServiceImpl(final RedisService redisService, final @Value("${muttley.security.jwt.token.expiration}") int expiration) {
        this.redisService = redisService;
        this.expiration = expiration;
    }

    @Override
    public void set(final String token, final JwtUser user) {
        this.redisService.set(token, user, expiration);
    }

    @Override
    public JwtUser get(final String token) {
        return (JwtUser) redisService.get(token);
    }

    @Override
    public boolean contains(final String token) {
        return redisService.hasKey(token);
    }
}
