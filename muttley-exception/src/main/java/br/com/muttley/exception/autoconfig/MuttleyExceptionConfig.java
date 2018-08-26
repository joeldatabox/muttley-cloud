package br.com.muttley.exception.autoconfig;

import br.com.muttley.exception.ErrorMessageBuilder;
import br.com.muttley.exception.property.MuttleyExceptionProperty;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.management.ManagementFactory;

/**
 * @author Joel Rodrigues Moreira on 18/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Configuration
@ComponentScan(basePackages = {
        "br.com.muttley.exception.controllers",
        "br.com.muttley.exception.feign",
        "br.com.muttley.exception.handlers"
})
@EnableConfigurationProperties(MuttleyExceptionProperty.class)
public class MuttleyExceptionConfig implements InitializingBean {

    @Bean
    public ErrorMessageBuilder errorMessageBuilderFactory() {
        return new ErrorMessageBuilder();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerFactory.getLogger("Configured exceptions handlers").info(ManagementFactory.getRuntimeMXBean().getName());
    }
}
