package br.com.muttley.security.zuul.gateway.service.config;

import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.auth.AuthenticationTokenServiceClient;
import br.com.muttley.security.infra.component.AuthenticationTokenFilterGateway;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import br.com.muttley.security.infra.service.CacheUserAuthenticationService;
import br.com.muttley.security.infra.service.impl.CacheUserAuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurações dos beans necessários para segurança do gateway
 *
 * @author Joel Rodrigues Moreira on 16/01/18.
 * @project spring-cloud
 */
@Configuration
public class WebSecurityConfig {

    @Bean
    @Autowired
    public AuthenticationTokenFilterGateway createAuthenticationTokenFilter(
            @Value("${muttley.security.jwt.controller.tokenHeader}") final String tokenHeader,
            final AuthenticationTokenServiceClient authenticationTokenServiceClient,
            final CacheUserAuthenticationService cacheAuth,
            final ApplicationEventPublisher eventPublisher) {
        return new AuthenticationTokenFilterGateway(tokenHeader, authenticationTokenServiceClient, cacheAuth, eventPublisher);
    }

    @Bean
    public UnauthorizedHandler createUnauthorizedHandler(@Value("${muttley.security.jwt.controller.loginEndPoint}") final String urlLogin) {
        return new UnauthorizedHandler(urlLogin);
    }

    @Bean
    @Autowired
    public CacheUserAuthenticationService createCacheUserAuthenticationService(final RedisService redisService, final @Value("${muttley.security.jwt.token.expiration}") int expiration, final ApplicationEventPublisher eventPublisher) {
        return new CacheUserAuthenticationServiceImpl(redisService, expiration, eventPublisher);
    }

}
