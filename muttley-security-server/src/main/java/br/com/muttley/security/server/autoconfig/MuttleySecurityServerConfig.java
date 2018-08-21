package br.com.muttley.security.server.autoconfig;

import br.com.muttley.security.server.autoconfig.mongo.MuttleyMongoConfig;
import br.com.muttley.security.server.autoconfig.security.MethodSecurityConfig;
import br.com.muttley.security.server.property.MuttleySecurityProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 18/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Configuration
@EnableConfigurationProperties(MuttleySecurityProperty.class)
@ComponentScan(basePackages = {
        "br.com.muttley.security.server.autoconfig.mongo",
        "br.com.muttley.security.server.autoconfig.security",
        "br.com.muttley.security.server.controller",
        "br.com.muttley.security.server.listeners",
        "br.com.muttley.security.server.service"
})
public class MuttleySecurityServerConfig {

    @Bean
    public MuttleyMongoConfig muttleyMongoConfigFactory() {
        return new MuttleyMongoConfig();
    }

    @Bean
    public MethodSecurityConfig methodSecurityConfigFactory() {
        return new MethodSecurityConfig();
    }

}
