package br.com.muttley.feign.service;

import feign.Feign;
import feign.Retryer;
import feign.codec.Decoder;
import feign.okhttp.OkHttpClient;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.cloud.netflix.feign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 23/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
public class FeignConfig extends FeignClientsConfiguration {
    private final String PROPERTY_SOURCE = "applicationConfig: [classpath:/bootstrap.properties]";
    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public Feign.Builder feignBuilder(final Retryer retryer, @Autowired ConfigurableEnvironment env) {
        final Map<String, Object> map = (Map<String, Object>) env.getPropertySources().get(PROPERTY_SOURCE).getSource();
        map.put("feign.okhttp.enabled", "true");

        //env.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE, map));
        return super.feignBuilder(retryer).client(new OkHttpClient());
    }

    @Override
    public Decoder feignDecoder() {
        final List<HttpMessageConverter<?>> decoderConverters = new ArrayList<>(messageConverters.getObject().getConverters());
        decoderConverters.add(new LongHttpMessageConverter());

        //HttpMessageConverters httpMessageConverters = new HttpMessageConverters(decoderConverters);

        return new SpringDecoder(() -> new HttpMessageConverters(decoderConverters));
    }
}