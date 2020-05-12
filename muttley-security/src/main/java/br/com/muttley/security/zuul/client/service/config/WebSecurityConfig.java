package br.com.muttley.security.zuul.client.service.config;

import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.UserPreferenceServiceClient;
import br.com.muttley.security.feign.WorkTeamServiceClient;
import br.com.muttley.security.infra.component.AuthenticationTokenFilterClient;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import br.com.muttley.security.infra.component.UserAfterCacheLoadListener;
import br.com.muttley.security.infra.service.AuthService;
import br.com.muttley.security.infra.service.CacheUserAuthenticationService;
import br.com.muttley.security.infra.service.impl.AuthServiceImpl;
import br.com.muttley.security.infra.service.impl.CacheUserAuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configurações dos beans necessários para segurança do client
 *
 * @author Joel Rodrigues Moreira on 18/01/18.
 * @project spring-cloud
 */
@Configuration
public class WebSecurityConfig {
    @Bean
    public UnauthorizedHandler createUnauthorizedHandler(@Value("${muttley.security.jwt.controller.loginEndPoint}") final String urlLogin) {
        return new UnauthorizedHandler(urlLogin);
    }

    @Bean
    @Autowired
    public AuthenticationTokenFilterClient createAuthenticationTokenFilterClient(@Value("${muttley.security.jwt.controller.tokenHeader-jwt:Authorization-jwt}") final String tokenHeader, final CacheUserAuthenticationService cacheAuth) {
        return new AuthenticationTokenFilterClient(tokenHeader, cacheAuth);
    }

    @Bean
    @Autowired
    public CacheUserAuthenticationService createCacheUserAuthenticationService(final RedisService redisService, final @Value("${muttley.security.jwt.token.expiration}") int expiration, final ApplicationEventPublisher eventPublisher) {
        return new CacheUserAuthenticationServiceImpl(redisService, expiration, eventPublisher);
    }

    @Bean
    @Primary
    public AuthService createAuthService(@Value("${muttley.security.jwt.controller.tokenHeader-jwt:Authorization-jwt}") final String tokenHeader, @Autowired final UserPreferenceServiceClient userPreferenceServiceClient) {
        return new AuthServiceImpl(tokenHeader, userPreferenceServiceClient);
    }

    @Bean
    @Autowired
    public UserAfterCacheLoadListener creaUserAfterCacheLoadListener(final UserPreferenceServiceClient userPreferenceServiceClient, final WorkTeamServiceClient workTeamServiceClient) {
        return new UserAfterCacheLoadListener(userPreferenceServiceClient, workTeamServiceClient);
    }
}
