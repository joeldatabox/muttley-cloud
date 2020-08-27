package br.com.muttley.feign.autoconfig;

import br.com.muttley.feign.property.MuttleyFeignProperty;
import feign.Request.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Joel Rodrigues Moreira on 20/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
@EnableConfigurationProperties(MuttleyFeignProperty.class)
public class FeignTimeoutConfig {
    @Autowired
    private MuttleyFeignProperty property;

    @Bean
    public Options options() {
        return new Options(property.getConnectTimeOutMillis(), MILLISECONDS, property.getReadTimeOutMillis(), MILLISECONDS, true);
    }
}
