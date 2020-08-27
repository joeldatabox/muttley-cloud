package br.com.muttley.feign.autoconfig;

import br.com.muttley.feign.converters.BooleanHttpMessageConverter;
import br.com.muttley.feign.converters.DateHttpMessageConverter;
import br.com.muttley.feign.converters.LongHttpMessageConverter;
import br.com.muttley.feign.property.MuttleyFeignProperty;
import br.com.muttley.feign.service.MuttleyPropagateHeadersService;
import br.com.muttley.feign.service.interceptors.PropagateHeadersInterceptor;
import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.codec.Decoder;
import feign.okhttp.OkHttpClient;
import feign.optionals.OptionalDecoder;
import feign.slf4j.Slf4jLogger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 23/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
@EnableConfigurationProperties(MuttleyFeignProperty.class)
public class FeignConfig extends FeignClientsConfiguration implements InitializingBean {
    private static final String PROPERTY_SOURCE = "applicationConfig: [classpath:/bootstrap.properties]";
    @Autowired
    private MuttleyFeignProperty property;
    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;
    @Autowired
    private ObjectProvider<MuttleyPropagateHeadersService> muttleyPropagateHeadersService;


    @Bean
    public Feign.Builder feignBuilder(final Retryer retryer, final @Autowired ConfigurableEnvironment env) {
        //final PropertySource<?> propertySource = env.getPropertySources().get(PROPERTY_SOURCE);
        final Feign.Builder builder = super.feignBuilder(retryer).client(new OkHttpClient());

        //injetando o serviço de headers a ser propagados
        final MuttleyPropagateHeadersService service = this.muttleyPropagateHeadersService.getIfAvailable();

        //foi injetado?
        if (service != null) {
            //adicionando o interceptor
            builder.requestInterceptor(new PropagateHeadersInterceptor(service));
        }

        /*if (propertySource != null) {
            final Map<String, Object> map = (Map<String, Object>) propertySource.getSource();
            map.put("feign.okhttp.enabled", "true");*/

        if (property.getLoggin().getLevel() != null && !Logger.Level.NONE.equals(property.getLoggin().getLevel())) {
            return builder.logger(new Slf4jLogger())
                    .logLevel(property.getLoggin().getLevel());
        }
        //}
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

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerFactory.getLogger(FeignConfig.class).info("Configured feign factory");
    }
}
