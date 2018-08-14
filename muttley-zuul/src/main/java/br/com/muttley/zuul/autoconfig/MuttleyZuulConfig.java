package br.com.muttley.zuul.autoconfig;

import br.com.muttley.zuul.components.LocationRewriteFilterOn201;
import br.com.muttley.zuul.components.LocationRewriteFilterOn30x;
import br.com.muttley.zuul.components.SessionSavingZuulPreFilter;
import br.com.muttley.zuul.property.MuttleySecurityProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MuttleySecurityProperty.class)
public class MuttleyZuulConfig {

    @Bean
    public LocationRewriteFilterOn30x createLocationRewriteFilterOn30x() {
        return new LocationRewriteFilterOn30x();
    }

    @Bean
    public LocationRewriteFilterOn201 createLocationRewriteFilterOn201() {
        return new LocationRewriteFilterOn201();
    }

    @Bean
    public SessionSavingZuulPreFilter createSessionSavingZuulPreFilter() {
        return new SessionSavingZuulPreFilter();
    }
}
