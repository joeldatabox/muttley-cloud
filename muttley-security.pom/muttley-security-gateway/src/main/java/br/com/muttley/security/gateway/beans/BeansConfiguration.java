package br.com.muttley.security.gateway.beans;

import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.gateway.MuttleyFeignSecurityAutoconfig;
import br.com.muttley.security.gateway.components.AuthenticationTokenFilterGateway;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import br.com.muttley.security.infra.feign.auth.AuthenticationTokenServiceClient;
import br.com.muttley.security.infra.services.CacheUserAuthenticationService;
import br.com.muttley.security.infra.services.impl.CacheUserAuthenticationServiceImpl;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
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
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoReactiveAutoConfiguration.class, GsonAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class})
public class BeansConfiguration {

    @Bean
    @Autowired
    public CacheUserAuthenticationService createCacheUserAuthenticationService(final RedisService redisService, final ApplicationEventPublisher eventPublisher) {
        return new CacheUserAuthenticationServiceImpl(redisService, eventPublisher);
    }

    @Bean
    @Autowired
    public AuthenticationTokenFilterGateway createAuthenticationTokenFilter(final AuthenticationTokenServiceClient authenticationTokenServiceClient,
                                                                            final CacheUserAuthenticationService cacheAuth,
                                                                            final ApplicationEventPublisher eventPublisher) {
        return new AuthenticationTokenFilterGateway(authenticationTokenServiceClient, cacheAuth, eventPublisher);
    }

    @Bean
    public UnauthorizedHandler createUnauthorizedHandler() {
        return new UnauthorizedHandler();
    }

}
