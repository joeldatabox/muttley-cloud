package br.com.muttley.hermes.config;

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
@EnableConfigurationProperties(MuttleyHermesProperty.class)
@EnableFeignClients(
        basePackages = {"br.com.muttley.hermes.api"}
)
public class MuttleyHermesAutoconfig implements InitializingBean {

    private final MuttleyHermesProperty property;

    @Autowired
    public MuttleyHermesAutoconfig(MuttleyHermesProperty property) {
        this.property = property;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerFactory.getLogger(MuttleyHermesAutoconfig.class).info("Configured API from " + this.property.getName());
    }
}
