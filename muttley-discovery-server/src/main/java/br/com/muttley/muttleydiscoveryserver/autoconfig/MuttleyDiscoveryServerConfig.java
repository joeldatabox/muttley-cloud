package br.com.muttley.muttleydiscoveryserver.autoconfig;

import br.com.muttley.muttleydiscoveryserver.property.EurekaConfigProperty;
import br.com.muttley.muttleydiscoveryserver.property.MuttleyConfigServerProperty;
import br.com.muttley.muttleydiscoveryserver.property.SeverConfigProperty;
import br.com.muttley.muttleydiscoveryserver.property.SpringConfigProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 02/04/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

@Configuration
@EnableConfigurationProperties(value = {
        EurekaConfigProperty.class,
        MuttleyConfigServerProperty.class,
        SeverConfigProperty.class,
        SpringConfigProperty.class
})
public class MuttleyDiscoveryServerConfig {

}
