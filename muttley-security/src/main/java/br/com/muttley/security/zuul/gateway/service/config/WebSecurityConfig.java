package br.com.muttley.security.zuul.gateway.service.config;

import br.com.muttley.localcache.services.LocalAPITokenService;
import br.com.muttley.localcache.services.LocalOwnerService;
import br.com.muttley.localcache.services.LocalRSAKeyPairService;
import br.com.muttley.localcache.services.LocalUserAuthenticationService;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.OwnerServiceClient;
import br.com.muttley.security.feign.UserServiceClient;
import br.com.muttley.security.feign.auth.AuthenticationTokenServiceClient;
import br.com.muttley.security.infra.component.AuthenticationTokenFilterGateway;
import br.com.muttley.security.infra.component.DeserializeUserPreferencesEventListener;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import br.com.muttley.security.infra.service.impl.LocalOwnerServiceImpl;
import br.com.muttley.security.infra.service.impl.LocalUserAuthenticationServiceImpl;
import br.com.muttley.security.zuul.gateway.listener.UserEventResolverListener;
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
    public AuthenticationTokenFilterGateway createAuthenticationTokenFilter(@Value("${muttley.security.jwt.controller.tokenHeader}") final String tokenHeader, @Value("${muttley.security.jwt.controller.xAPITokenHeader:X-Api-Token}") final String xAPIToken, final LocalUserAuthenticationService localUserAuthentication, final LocalAPITokenService localAPITokenService) {
        return new AuthenticationTokenFilterGateway(tokenHeader, xAPIToken, localUserAuthentication, localAPITokenService);
    }

    @Bean
    @Autowired
    public LocalUserAuthenticationService createLocalUserAuthenticationService(final RedisService redisService, final AuthenticationTokenServiceClient authenticationTokenService, final LocalRSAKeyPairService rsaKeyPairService, final ApplicationEventPublisher eventPublisher) {
        return new LocalUserAuthenticationServiceImpl(redisService, authenticationTokenService, rsaKeyPairService, eventPublisher);
    }

    @Bean
    public UnauthorizedHandler createUnauthorizedHandler(@Value("${muttley.security.jwt.controller.loginEndPoint}") final String urlLogin) {
        return new UnauthorizedHandler(urlLogin);
    }

    @Bean
    @Autowired
    public LocalOwnerService createLocalOwnerService(final RedisService redisService, final OwnerServiceClient ownerServiceClient) {
        return new LocalOwnerServiceImpl(redisService, ownerServiceClient);
    }

    @Bean
    @Autowired
    public DeserializeUserPreferencesEventListener createUserPreferencesResolverEventListener(final LocalOwnerService ownerService) {
        return new DeserializeUserPreferencesEventListener(ownerService);
    }


    @Bean
    @Autowired
    public UserEventResolverListener createUserEventResolverListener(final UserServiceClient userServiceClient) {
        return new UserEventResolverListener(userServiceClient);
    }
}
