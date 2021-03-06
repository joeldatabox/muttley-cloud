package br.com.muttley.zuul.autoconfig;

import br.com.muttley.zuul.components.LocationRewriteFilterOn201;
import br.com.muttley.zuul.components.LocationRewriteFilterOn30x;
import br.com.muttley.zuul.components.SessionSavingZuulPreFilter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableZuulProxy
public class MuttleyZuulConfig implements InitializingBean {

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

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerFactory.getLogger(MuttleyZuulConfig.class).info("Configure basic Zull filters");
    }
}
