package br.com.muttley.feign.autoconfig;

import br.com.muttley.feign.converters.BooleanHttpMessageConverter;
import br.com.muttley.feign.converters.DateHttpMessageConverter;
import br.com.muttley.feign.converters.LongHttpMessageConverter;
import br.com.muttley.feign.property.MuttleyFeignProperty;
import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.codec.Decoder;
import feign.okhttp.OkHttpClient;
import feign.optionals.OptionalDecoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
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
@EnableConfigurationProperties(MuttleyFeignProperty.class)
public class FeignConfig extends FeignClientsConfiguration {
    private static final String PROPERTY_SOURCE = "applicationConfig: [classpath:/bootstrap.properties]";
    @Autowired
    private MuttleyFeignProperty property;
    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;


    @Bean
    public Feign.Builder feignBuilder(final Retryer retryer, final @Autowired ConfigurableEnvironment env) {
        final PropertySource<?> propertySource = env.getPropertySources().get(PROPERTY_SOURCE);
        final Feign.Builder builder = super.feignBuilder(retryer).client(new OkHttpClient());
        if (propertySource != null) {
            final Map<String, Object> map = (Map<String, Object>) propertySource.getSource();

            if (property.getLoggin().getLevel() != null && !Logger.Level.NONE.equals(property.getLoggin().getLevel())) {
                map.put("feign.okhttp.enabled", "true");
                return builder.logger(new Slf4jLogger())
                        .logLevel(property.getLoggin().getLevel());
            }
        }
        return builder;
    }

    @Override
    public Decoder feignDecoder() {
        final List<HttpMessageConverter<?>> decoderConverters = new ArrayList<>(messageConverters.getObject().getConverters());
        decoderConverters.add(new LongHttpMessageConverter());
        decoderConverters.add(new BooleanHttpMessageConverter());
        decoderConverters.add(new DateHttpMessageConverter());

        return new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(() -> new HttpMessageConverters(decoderConverters))));
        //return new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(this.messageConverters)));
    }
}