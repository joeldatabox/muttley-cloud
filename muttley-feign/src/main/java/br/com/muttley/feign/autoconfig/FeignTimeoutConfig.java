package br.com.muttley.feign.autoconfig;

import br.com.muttley.feign.property.MuttleyFeignProperty;
import feign.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    @ConditionalOnMissingBean
    public Request.Options options() {
        return new Request.Options(property.getConnectTimeOutMillis(), property.getReadTimeOutMillis());
    }
}
