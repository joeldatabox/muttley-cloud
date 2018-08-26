package br.com.muttley.mongo.autoconfig;

import br.com.muttley.mongo.mongoconfig.MuttleyMongoMultiTenancyConfig;
import br.com.muttley.mongo.mongoconfig.MuttleyMongoSimpleTenancyConfig;
import br.com.muttley.mongo.properties.MuttleyMongoProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 26/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Configuration
@EnableConfigurationProperties(MuttleyMongoProperties.class)
public class MuttleyMongoConfig {

    @Bean
    @ConditionalOnProperty(name = "muttley.mongo.strategy", havingValue = "simpletenancy")
    public MuttleyMongoSimpleTenancyConfig muttleyMongoSimpleTenancyConfigFactory() {
        return new MuttleyMongoSimpleTenancyConfig();
    }

    @Bean
    @ConditionalOnProperty(name = "muttley.mongo.strategy", havingValue = "multitenancy")
    public MuttleyMongoMultiTenancyConfig muttleyMongoMultiTenancyConfigFactory() {
        return new MuttleyMongoMultiTenancyConfig();
    }
}
