package br.com.muttley.feign.service;

import feign.Feign;
import feign.Retryer;
import feign.okhttp.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 23/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
public class FeignConfig extends FeignClientsConfiguration {
    private final String PROPERTY_SOURCE = "applicationConfig: [classpath:/bootstrap.properties]";

    @Bean
    public Feign.Builder feignBuilder(final Retryer retryer, @Autowired ConfigurableEnvironment env) {
        final Map<String, Object> map = (Map<String, Object>) env.getPropertySources().get(PROPERTY_SOURCE).getSource();
        map.put("feign.okhttp.enabled", "true");

        env.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE, map));
        return super.feignBuilder(retryer).client(new OkHttpClient());
    }
}