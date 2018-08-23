package br.com.muttley.security.zuul.client.config;

import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.UserPreferenceServiceClient;
import br.com.muttley.security.feign.WorkTeamServiceClient;
import br.com.muttley.security.infra.component.AuthenticationTokenFilterClient;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import br.com.muttley.security.infra.component.UserAfterCacheLoadListener;
import br.com.muttley.security.properties.MuttleySecurityProperty;
import br.com.muttley.security.infra.service.AuthService;
import br.com.muttley.security.infra.service.CacheUserAuthenticationService;
import br.com.muttley.security.infra.service.impl.AuthServiceImpl;
import br.com.muttley.security.infra.service.impl.CacheUserAuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurações dos beans necessários para segurança do client
 *
 * @author Joel Rodrigues Moreira on 18/01/18.
 * @project spring-cloud
 */
@Configuration
public class WebSecurityClientConfig {

    @Autowired
    private MuttleySecurityProperty property;

    @Bean
    public UnauthorizedHandler createUnauthorizedHandler() {
        return new UnauthorizedHandler();
    }

    @Bean
    public AuthenticationTokenFilterClient createAuthenticationTokenFilterClient(@Autowired final CacheUserAuthenticationService cacheAuth) {
        return new AuthenticationTokenFilterClient(cacheAuth);
    }

    @Bean
    public CacheUserAuthenticationService createCacheUserAuthenticationService(@Autowired final RedisService redisService, @Autowired final ApplicationEventPublisher eventPublisher) {
        return new CacheUserAuthenticationServiceImpl(redisService, eventPublisher);
    }

    @Bean
    public AuthService createAuthService() {
        return new AuthServiceImpl();
    }

    @Bean
    public UserAfterCacheLoadListener creaUserAfterCacheLoadListener(@Autowired final UserPreferenceServiceClient userPreferenceServiceClient, @Autowired final WorkTeamServiceClient workTeamServiceClient) {
        return new UserAfterCacheLoadListener(userPreferenceServiceClient, workTeamServiceClient);
    }
}
