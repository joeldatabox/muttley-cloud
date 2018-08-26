package br.com.muttley.security.zuul.client.config;

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
import br.com.muttley.security.properties.MuttleySecurityProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Configurações dos beans necessários para segurança do client
 *
 * @author Joel Rodrigues Moreira on 18/01/18.
 * @project spring-cloud
 */
@Configuration
public class WebSecurityClientConfig implements InitializingBean {

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

    @Override
    public void afterPropertiesSet() throws Exception {
        final Logger log = LoggerFactory.getLogger(WebSecurityClientConfig.class);
        if (isEmpty(property.getSecurityServer().getNameServer())) {
            log.error("Please, set property ${muttley.security-server.name-server}");
        }
    }
}
