package br.com.muttley.security.zuul.client.service.config;

import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.UserDataBindingClient;
import br.com.muttley.security.feign.UserPreferenceServiceClient;
import br.com.muttley.security.feign.auth.AuthenticationTokenServiceClient;
import br.com.muttley.security.infra.component.AuthenticationTokenFilterClient;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import br.com.muttley.security.infra.component.UserAfterCacheLoadListener;
import br.com.muttley.security.infra.service.AuthService;
import br.com.muttley.security.infra.service.LocalDatabindingService;
import br.com.muttley.security.infra.service.LocalOwnerService;
import br.com.muttley.security.infra.service.LocalRolesService;
import br.com.muttley.security.infra.service.LocalUserAuthenticationService;
import br.com.muttley.security.infra.service.LocalUserPreferenceService;
import br.com.muttley.security.infra.service.impl.AuthServiceImpl;
import br.com.muttley.security.infra.service.impl.LocalUserAuthenticationServiceImpl;
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
    public AuthenticationTokenFilterClient createAuthenticationTokenFilterClient(
            @Value("${muttley.security.jwt.controller.tokenHeader-jwt:Authorization-jwt}") final String tokenHeader,
            final LocalUserAuthenticationService localUserAuthentication) {
        return new AuthenticationTokenFilterClient(tokenHeader, localUserAuthentication);
    }

    @Bean
    @Autowired
    public LocalUserAuthenticationService createLocalUserAuthenticationService(final RedisService redisService, final @Value("${muttley.security.jwt.token.expiration}") int expiration, final AuthenticationTokenServiceClient authenticationTokenService, final ApplicationEventPublisher eventPublisher) {
        return new LocalUserAuthenticationServiceImpl(redisService, authenticationTokenService, eventPublisher);
    }

    @Bean
    @Primary
    public AuthService createAuthService(@Value("${muttley.security.jwt.controller.tokenHeader-jwt:Authorization-jwt}") final String tokenHeader, @Autowired final UserPreferenceServiceClient userPreferenceServiceClient, final UserDataBindingClient dataBindingClient) {
        return new AuthServiceImpl(tokenHeader, userPreferenceServiceClient, dataBindingClient);
    }

    @Bean
    @Autowired
    public UserAfterCacheLoadListener creaUserAfterCacheLoadListener(final LocalUserPreferenceService userPreferenceService, final LocalOwnerService ownerService, final LocalRolesService rolesService, final LocalDatabindingService localDatabindingService) {
        return new UserAfterCacheLoadListener(userPreferenceService, ownerService, rolesService, localDatabindingService);
    }
}
