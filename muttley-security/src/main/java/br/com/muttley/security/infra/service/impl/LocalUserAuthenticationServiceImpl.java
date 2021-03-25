package br.com.muttley.security.infra.service.impl;

import br.com.muttley.localcache.services.LocalUserAuthenticationService;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.events.UserAfterCacheLoadEvent;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.auth.AuthenticationTokenServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira 24/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class LocalUserAuthenticationServiceImpl implements LocalUserAuthenticationService {
    private static final String BASIC_KEY = "JWT-TOKEN:";
    private final RedisService redisService;
    private final AuthenticationTokenServiceClient tokenServiceClient;
    private final LocalJwtTokenUtilService utilService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public LocalUserAuthenticationServiceImpl(final RedisService redisService, final AuthenticationTokenServiceClient tokenServiceClient, final ApplicationEventPublisher eventPublisher) {
        this.redisService = redisService;
        this.tokenServiceClient = tokenServiceClient;
        this.utilService = new LocalJwtTokenUtilService(redisService);
        this.eventPublisher = eventPublisher;
    }


    @Override
    public JwtUser getJwtUserFrom(final JwtToken token) {
        //verfificando se o token informado é válido
        if (this.utilService.isValidToken(token.getToken())) {
            final JwtUser jwtUser;
            //verificando se já existe esse token salvo no redis
            if (this.redisService.hasKey(this.getBasicKey(token))) {
                //caso exista recuperamos localmente as infos
                jwtUser = (JwtUser) redisService.get(this.getBasicKey(token));
            } else {
                //se ainda não existe devemos buscar do servidor de segurança
                jwtUser = this.tokenServiceClient.getUserFromToken(token);
                //salvando o usuário recuperado no cache
                this.set(token, jwtUser);
            }
            if (jwtUser != null) {
                //Notificando que foi carregado um usuário do cache do sistema
                this.eventPublisher.publishEvent(new UserAfterCacheLoadEvent(token, jwtUser.getOriginUser()));
            }
            return jwtUser;
        } else {
            //se chegou aqui, logo podemos inferir que o token é inválido
            //logo pode ser removido
            this.remove(token);
            redisService.delete(token.getToken());
        }
        return null;
    }

    private void set(final JwtToken token, final JwtUser user) {
        this.redisService.set(this.getBasicKey(token), user, token.getExpiration());
    }

    @Override
    public LocalUserAuthenticationService remove(final JwtToken token) {
        if (token != null && !token.isEmpty()) {
            this.redisService.delete(token.getToken());
        }
        return this;
    }

    @Override
    public void refreshToken(final JwtToken currentToken, final JwtToken newToken) {
        //verificando se o novo token é válido
        if (this.utilService.isValidToken(newToken.getToken())) {
            //ronomenando a chave de acesso caso existe em cache
            this.redisService.changeKey(this.getBasicKey(currentToken), this.getBasicKey(newToken));
        }
    }

    private String getBasicKey(final JwtToken token) {
        return BASIC_KEY + token.getToken();
    }


}
