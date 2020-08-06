package br.com.muttley.notification.onesignal.configuration;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
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
public class MuttleyOneSignalAutoconfig implements InitializingBean {

    private final MuttleyOneSignalProperty property;

    @Autowired
    public MuttleyOneSignalAutoconfig(MuttleyOneSignalProperty property) {
        this.property = property;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerFactory.getLogger(MuttleyOneSignalAutoconfig.class).info("Configured OneSignal message");
    }
}
