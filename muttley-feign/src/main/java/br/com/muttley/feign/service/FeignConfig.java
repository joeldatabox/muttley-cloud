package br.com.muttley.feign.service;

import br.com.muttley.feign.service.converters.BooleanHttpMessageConverter;
import br.com.muttley.feign.service.converters.DateHttpMessageConverter;
import br.com.muttley.feign.service.converters.LongHttpMessageConverter;
import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.codec.Decoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 23/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
public class FeignConfig extends FeignClientsConfiguration {
    private final String PROPERTY_SOURCE = "applicationConfig: [classpath:/bootstrap.properties]";
    @Autowired
    private ObjectFactory<HttpMessageConverter> messageConverters;


    @Bean
    public Feign.Builder feignBuilder(
            final Retryer retryer,
            final @Autowired ConfigurableEnvironment env,
            final @Value("${muttley.feign.loggin.level:#{null}}") String logLevel) {
        final Map<String, Object> map = (Map<String, Object>) env.getPropertySources().get(PROPERTY_SOURCE).getSource();
        map.put("feign.okhttp.enabled", "true");

        return includeLogger(logLevel, super.feignBuilder(retryer).client(new OkHttpClient()));
    }

    @Override
    public Decoder feignDecoder() {
        final List<HttpMessageConverter<?>> decoderConverters = new ArrayList<>(messageConverters.getObject().getSupportedMediaTypes());
        decoderConverters.add(new LongHttpMessageConverter());
        decoderConverters.add(new BooleanHttpMessageConverter());
        decoderConverters.add(new DateHttpMessageConverter());

        //HttpMessageConverters httpMessageConverters = new HttpMessageConverters(decoderConverters);

        return new SpringDecoder(() -> new HttpMessageConverters(decoderConverters));
    }

    /**
     * Verifica se é necessário incluir algum log no sistema
     */
    private Feign.Builder includeLogger(final String logLevel, final Feign.Builder builder) {
        Logger.Level level = Logger.Level.NONE;
        if (!isEmpty(logLevel)) {
            switch (logLevel) {
                case "BASIC":
                    level = Logger.Level.BASIC;
                    break;
                case "HEADERS":
                    level = Logger.Level.HEADERS;
                    break;
                case "FULL":
                    level = Logger.Level.FULL;
                default:
                    level = Logger.Level.NONE;
            }
        }
        if (level != Logger.Level.NONE) {
            return builder.logger(new Slf4jLogger())
                    .logLevel(level);
        }
        return builder;
    }
}