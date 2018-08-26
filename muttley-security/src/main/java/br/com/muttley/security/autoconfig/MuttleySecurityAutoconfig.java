package br.com.muttley.security.autoconfig;

import br.com.muttley.security.properties.MuttleySecurityProperty;
import br.com.muttley.security.zuul.client.config.WebSecurityClientConfig;
import br.com.muttley.security.zuul.gateway.config.WebSecurityGatewayConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 22/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Configuration
@AutoConfigureAfter(MuttleyFeignSecurityAutoconfig.class)
@EnableConfigurationProperties(MuttleySecurityProperty.class)
public class MuttleySecurityAutoconfig {

    @Bean
    @ConditionalOnProperty(name = "muttley.module", havingValue = "client")
    public WebSecurityClientConfig webSecurityClientConfigFactory() {
        return new WebSecurityClientConfig();
    }

    @Bean
    @ConditionalOnProperty(name = "muttley.module", havingValue = "gateway")
    public WebSecurityGatewayConfig webSecurityGatewayConfigFactory() {
        return new WebSecurityGatewayConfig();
    }
}
