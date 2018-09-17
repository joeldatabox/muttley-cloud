package br.com.muttley.security.infra.services.impl;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.events.UserAfterCacheLoadEvent;
import br.com.muttley.model.security.events.UserBeforeCacheSaveEvent;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.infra.services.CacheUserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static br.com.muttley.security.infra.properties.Properties.TOKE_EXPIRATION;

/**
 * @author Joel Rodrigues Moreira on 09/01/18.
 * @project demo
 */
@Service
public class CacheUserAuthenticationServiceImpl implements CacheUserAuthenticationService {

    private final RedisService redisService;
    private final ApplicationEventPublisher eventPublisher;
    private final int tokenExpiration;

    @Autowired
    public CacheUserAuthenticationServiceImpl(final RedisService redisService, final ApplicationEventPublisher eventPublisher, @Value(TOKE_EXPIRATION) final int tokenExpiration) {
        this.redisService = redisService;
        this.eventPublisher = eventPublisher;
        this.tokenExpiration = tokenExpiration;
    }

    @Override
    public void set(final String token, final JwtUser user) {
        //notificando que será salvo um usuário no cache do sistema
        this.eventPublisher.publishEvent(new UserBeforeCacheSaveEvent(user.getOriginUser()));
        this.redisService.set(token, user, this.tokenExpiration);
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

    @Override
    public void remove(final JwtToken token) {
        this.redisService.delete(token.getToken());
    }

    @Override
    public boolean refreshToken(final JwtToken currentToken, final JwtToken newToken) {
        //verificando se o token exite
        if (contains(currentToken.getToken())) {
            //alterando o token do usuário
            set(newToken.getToken(), get(currentToken.getToken()));
            //removendo token anterior
            remove(currentToken);
            return true;
        }
        return false;
    }
}
