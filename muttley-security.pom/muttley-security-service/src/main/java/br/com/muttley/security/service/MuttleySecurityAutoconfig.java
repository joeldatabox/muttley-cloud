package br.com.muttley.security.service;


import br.com.muttley.feign.autoconfig.FeignConfig;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import br.com.muttley.security.infra.component.UserAfterCacheLoadListener;
import br.com.muttley.security.infra.feign.UserPreferenceServiceClient;
import br.com.muttley.security.infra.feign.UserServiceClient;
import br.com.muttley.security.infra.feign.WorkTeamServiceClient;
import br.com.muttley.security.infra.services.AuthService;
import br.com.muttley.security.infra.services.CacheUserAuthenticationService;
import br.com.muttley.security.infra.services.CacheUserPreferences;
import br.com.muttley.security.infra.services.impl.AuthServiceImpl;
import br.com.muttley.security.infra.services.impl.CacheUserAuthenticationServiceImpl;
import br.com.muttley.security.infra.services.impl.CacheUserPreferencesImpl;
import br.com.muttley.security.service.components.AuthenticationTokenFilterClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static br.com.muttley.security.infra.properties.Properties.LOGIN_END_POINT;
import static br.com.muttley.security.infra.properties.Properties.TOKEN_HEADER_JWT;
import static br.com.muttley.security.infra.properties.Properties.TOKE_EXPIRATION;

@Configuration
@AutoConfigureAfter(FeignConfig.class)
@ComponentScan(basePackages = {
        "br.com.muttley.security.service.config"
})
public class MuttleySecurityAutoconfig implements InitializingBean {


    @Bean
    public UnauthorizedHandler createUnauthorizedHandler(@Value(LOGIN_END_POINT) final String loginEndpoint) {
        return new UnauthorizedHandler(loginEndpoint);
    }

    @Bean
    public AuthenticationTokenFilterClient createAuthenticationTokenFilterClient(final @Value(TOKEN_HEADER_JWT) String tokenHeader, @Autowired final CacheUserAuthenticationService cacheAuth) {
        return new AuthenticationTokenFilterClient(tokenHeader, cacheAuth);
    }

    @Bean
    public CacheUserAuthenticationService createCacheUserAuthenticationService(@Autowired final RedisService redisService, @Autowired final ApplicationEventPublisher eventPublisher, @Value(TOKE_EXPIRATION) final int tokenExpiration) {
        return new CacheUserAuthenticationServiceImpl(redisService, eventPublisher, tokenExpiration);
    }

    @Bean
    public AuthService createAuthService(@Value(TOKEN_HEADER_JWT) final String tokenHeader, @Autowired UserServiceClient userServiceClient) {
        return new AuthServiceImpl(tokenHeader, userServiceClient);
    }

    @Bean
    public CacheUserPreferences createCacheUserPreferences(@Autowired RedisService redisService) {
        return new CacheUserPreferencesImpl(redisService);
    }

    @Bean
    public UserAfterCacheLoadListener createUserAfterCacheLoadListener(@Autowired final UserPreferenceServiceClient userPreferenceServiceClient, @Autowired final WorkTeamServiceClient workTeamServiceClient, @Autowired final CacheUserPreferences cacheUserPreferences) {
        return new UserAfterCacheLoadListener(userPreferenceServiceClient, workTeamServiceClient, cacheUserPreferences);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final Logger log = LoggerFactory.getLogger(MuttleySecurityAutoconfig.class);
        /*if (isEmpty(property.getSecurityServer().getNameServer())) {
            log.error("Please, set property ${muttley.security-server.name-server}");
        } else {
            log.info("Configured SpringSecuryt for service-client");
        }*/
    }
}