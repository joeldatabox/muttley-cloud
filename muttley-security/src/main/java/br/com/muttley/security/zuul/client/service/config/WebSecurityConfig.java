package br.com.muttley.security.zuul.client.service.config;

import br.com.muttley.localcache.services.LocalDatabindingService;
import br.com.muttley.localcache.services.LocalOwnerService;
import br.com.muttley.localcache.services.LocalRolesService;
import br.com.muttley.localcache.services.LocalUserAuthenticationService;
import br.com.muttley.localcache.services.LocalUserPreferenceService;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.OwnerServiceClient;
import br.com.muttley.security.feign.UserDataBindingClient;
import br.com.muttley.security.feign.UserPreferenceServiceClient;
import br.com.muttley.security.feign.PassaportServiceClient;
import br.com.muttley.security.feign.auth.AuthenticationTokenServiceClient;
import br.com.muttley.security.infra.component.AuthenticationTokenFilterClient;
import br.com.muttley.security.infra.component.DeserializeUserPreferencesEventListener;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import br.com.muttley.security.infra.component.UserAfterCacheLoadListener;
import br.com.muttley.security.infra.service.AuthService;
import br.com.muttley.security.infra.service.impl.AuthServiceImpl;
import br.com.muttley.security.infra.service.impl.LocalDatabindingServiceImpl;
import br.com.muttley.security.infra.service.impl.LocalOwnerServiceImpl;
import br.com.muttley.security.infra.service.impl.LocalRolesServiceImpl;
import br.com.muttley.security.infra.service.impl.LocalUserAuthenticationServiceImpl;
import br.com.muttley.security.infra.service.impl.LocalUserPrefenceServiceImpl;
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
    public LocalUserAuthenticationService createLocalUserAuthenticationService(final RedisService redisService, final AuthenticationTokenServiceClient authenticationTokenService, final ApplicationEventPublisher eventPublisher) {
        return new LocalUserAuthenticationServiceImpl(redisService, authenticationTokenService, eventPublisher);
    }

    @Bean
    @Primary
    public AuthService createAuthService(@Value("${muttley.security.jwt.controller.tokenHeader-jwt:Authorization-jwt}") final String tokenHeader, @Autowired final UserPreferenceServiceClient userPreferenceServiceClient, final UserDataBindingClient dataBindingClient, final LocalUserAuthenticationService localUserAuthenticationService) {
        return new AuthServiceImpl(tokenHeader, userPreferenceServiceClient, dataBindingClient, localUserAuthenticationService);
    }

    @Bean
    @Autowired
    public UserAfterCacheLoadListener creaUserAfterCacheLoadListener(final LocalUserPreferenceService userPreferenceService, final LocalOwnerService ownerService, final LocalRolesService rolesService, final LocalDatabindingService localDatabindingService) {
        return new UserAfterCacheLoadListener(userPreferenceService, ownerService, rolesService, localDatabindingService);
    }

    @Bean
    @Autowired
    public LocalUserPreferenceService createLocalUserPreferenceService(final RedisService redisService, final UserPreferenceServiceClient userPreferenceServiceClient, final OwnerServiceClient ownerServiceClient, final ApplicationEventPublisher publisher) {
        return new LocalUserPrefenceServiceImpl(redisService, userPreferenceServiceClient, ownerServiceClient, publisher);
    }

    @Bean
    @Autowired
    public LocalOwnerService createLocalOwnerService(final RedisService redisService, final OwnerServiceClient ownerServiceClient) {
        return new LocalOwnerServiceImpl(redisService, ownerServiceClient);
    }

    @Bean
    @Autowired
    public LocalRolesService createLocalRolesService(final RedisService redisService, final PassaportServiceClient passaportServiceClient) {
        return new LocalRolesServiceImpl(redisService, passaportServiceClient);
    }

    @Bean
    @Autowired
    public LocalDatabindingService createLocalDatabindingService(final RedisService redisService, final UserDataBindingClient userDataBindingClient) {
        return new LocalDatabindingServiceImpl(redisService, userDataBindingClient);
    }

    @Bean
    @Autowired
    public DeserializeUserPreferencesEventListener createUserPreferencesResolverEventListener(final LocalOwnerService ownerService) {
        return new DeserializeUserPreferencesEventListener(ownerService);
    }
}
