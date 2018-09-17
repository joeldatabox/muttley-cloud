package br.com.muttley.security.gateway;

import br.com.muttley.feign.autoconfig.FeignConfig;
import br.com.muttley.security.gateway.properties.MuttleySecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 31/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Configuration
@EnableConfigurationProperties(MuttleySecurityProperties.class)
@AutoConfigureAfter(FeignConfig.class)
@ComponentScan(basePackages = {
        "br.com.muttley.security.gateway.config",
        "br.com.muttley.security.gateway.beans",
        "br.com.muttley.security.gateway.controller"
})
public class MuttleySecurityAutoconfig {
    public MuttleySecurityAutoconfig() {
    }

}
