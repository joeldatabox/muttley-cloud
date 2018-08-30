package br.com.muttley.security.zuul.gateway.config;

import br.com.muttley.feign.autoconfig.FeignConfig;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.auth.AuthenticationTokenServiceClient;
import br.com.muttley.security.infra.component.AuthenticationTokenFilterGateway;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import br.com.muttley.security.infra.service.CacheUserAuthenticationService;
import br.com.muttley.security.infra.service.impl.CacheUserAuthenticationServiceImpl;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configurações dos beans necessários para segurança do gateway
 *
 * @author Joel Rodrigues Moreira on 16/01/18.
 * @project spring-cloud
 */
@Configuration
@AutoConfigureAfter(FeignConfig.class)
/*@ComponentScan(basePackageClasses = CacheUserAuthenticationService.class)*/
public class WebSecurityGatewayConfig implements InitializingBean {

    @Bean
    @Autowired
    public CacheUserAuthenticationService createCacheUserAuthenticationService(final RedisService redisService, final ApplicationEventPublisher eventPublisher) {
        return new CacheUserAuthenticationServiceImpl(redisService, eventPublisher);
    }

    @Bean
    @Autowired
    public AuthenticationTokenFilterGateway createAuthenticationTokenFilter(
            final AuthenticationTokenServiceClient authenticationTokenServiceClient,
            final CacheUserAuthenticationService cacheAuth,
            final ApplicationEventPublisher eventPublisher) {
        return new AuthenticationTokenFilterGateway(authenticationTokenServiceClient, cacheAuth, eventPublisher);
    }

    @Bean
    public UnauthorizedHandler createUnauthorizedHandler() {
        return new UnauthorizedHandler();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerFactory.getLogger(WebSecurityGatewayConfig.class).info("Configured SpringSecuryt for gateway-service");
    }
}
