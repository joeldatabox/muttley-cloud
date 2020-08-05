package br.com.muttley.notification.onesignal.configuration;

import br.com.muttley.feign.autoconfig.FeignConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 02/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
@EnableConfigurationProperties(MuttleyOneSignalProperty.class)
@EnableFeignClients(
        basePackages = {"br.com.muttley.notification.onesignal.service"}
)
public class MuttleyOneSignalConfig implements InitializingBean {

    private final MuttleyOneSignalProperty property;

    @Autowired
    public MuttleyOneSignalConfig(MuttleyOneSignalProperty property) {
        this.property = property;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerFactory.getLogger(FeignConfig.class).info("Configured OneSignal message");
    }
}
