package br.com.muttley.security.gateway.beans;

import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.infra.component.AuthenticationTokenFilterGateway;
import br.com.muttley.security.gateway.config.MuttleyFeignSecurityAutoconfig;
import br.com.muttley.security.gateway.properties.MuttleySecurityProperties;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import br.com.muttley.security.infra.feign.auth.AuthenticationTokenServiceClient;
import br.com.muttley.security.infra.services.CacheUserAuthenticationService;
import br.com.muttley.security.infra.services.impl.CacheUserAuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 30/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Configuration
@AutoConfigureAfter(MuttleyFeignSecurityAutoconfig.class)
public class BeansConfiguration {

    @Bean
    @Autowired
    public CacheUserAuthenticationService createCacheUserAuthenticationService(final RedisService redisService, final ApplicationEventPublisher eventPublisher, final MuttleySecurityProperties properties) {
        return new CacheUserAuthenticationServiceImpl(redisService, eventPublisher, properties.getSecurity().getJwt().getToken().getExpiration());
    }

    @Bean
    @Autowired
    public AuthenticationTokenFilterGateway createAuthenticationTokenFilter(final AuthenticationTokenServiceClient authenticationTokenServiceClient,
                                                                            final CacheUserAuthenticationService cacheAuth,
                                                                            final ApplicationEventPublisher eventPublisher) {
        return new AuthenticationTokenFilterGateway(authenticationTokenServiceClient, cacheAuth, eventPublisher);
    }

    @Bean
    @Autowired
    public UnauthorizedHandler createUnauthorizedHandler(final MuttleySecurityProperties properties) {
        return new UnauthorizedHandler(properties.getSecurity().getJwt().getController().getLoginEndPoint());
    }

}
