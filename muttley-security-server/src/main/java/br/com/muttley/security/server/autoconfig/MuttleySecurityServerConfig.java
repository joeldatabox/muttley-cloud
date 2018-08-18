package br.com.muttley.security.server.autoconfig;

import br.com.muttley.security.server.autoconfig.mongo.MuttleyMongoConfig;
import br.com.muttley.security.server.autoconfig.security.MethodSecurityConfig;
import br.com.muttley.security.server.listeners.AccessPlanEventResolverListener;
import br.com.muttley.security.server.listeners.OwnerCreateEventListener;
import br.com.muttley.security.server.listeners.UserEventResolverListener;
import br.com.muttley.security.server.property.MuttleySecurityProperty;
import br.com.muttley.security.server.service.AccessPlanService;
import br.com.muttley.security.server.service.UserService;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 18/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Configuration
@EnableConfigurationProperties(MuttleySecurityProperty.class)
public class MuttleySecurityServerConfig {

    @Bean
    @ConditionalOnMissingBean
    public MuttleyMongoConfig muttleyMongoConfigFactory() {
        return new MuttleyMongoConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public MethodSecurityConfig methodSecurityConfigFactory() {
        return new MethodSecurityConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessPlanEventResolverListener accessPlanEventResolverListenerFactory(@Autowired final AccessPlanService service) {
        return new AccessPlanEventResolverListener(service);
    }

    @Bean
    @ConditionalOnMissingBean
    public OwnerCreateEventListener ownerCreateEventListenerFactory(@Autowired final WorkTeamService workTeamService, @Autowired final UserService userService) {
        return new OwnerCreateEventListener(workTeamService, userService);
    }

    @Bean
    @ConditionalOnMissingBean
    public UserEventResolverListener userEventResolverListenerFactory(@Autowired final UserService userService) {
        return new UserEventResolverListener(userService);
    }

}
