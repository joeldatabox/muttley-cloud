package br.com.muttley.security.infra.service.impl;

import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.events.UserAfterCacheLoadEvent;
import br.com.muttley.model.security.events.UserBeforeCacheSaveEvent;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.infra.service.CacheUserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira on 09/01/18.
 * @project demo
 */
@Service
public class CacheUserAuthenticationServiceImpl implements CacheUserAuthenticationService {

    private final RedisService redisService;
    private final ApplicationEventPublisher eventPublisher;
    private final int expiration;

    @Autowired
    public CacheUserAuthenticationServiceImpl(final RedisService redisService, final @Value("${muttley.security.jwt.token.expiration}") int expiration, final ApplicationEventPublisher eventPublisher) {
        this.redisService = redisService;
        this.expiration = expiration;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void set(final String token, final JwtUser user) {
        //notificando que será salvo um usuário no cache do sistema
        this.eventPublisher.publishEvent(new UserBeforeCacheSaveEvent(user.getOriginUser()));
        this.redisService.set(token, user, expiration);
    }

    @Override
    public JwtUser get(final String token) {
        final JwtUser jwtUser = (JwtUser) redisService.get(token);
        if (jwtUser != null) {
            //Notificando que foi carregado um usuário do cache do sistema
            this.eventPublisher.publishEvent(new UserAfterCacheLoadEvent(jwtUser.getOriginUser()));
        }
        return jwtUser;
    }

    @Override
    public boolean contains(final String token) {
        return redisService.hasKey(token);
    }
}
