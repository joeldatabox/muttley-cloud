package br.com.muttley.notification.twilio.config;

import br.com.muttley.notification.twilio.service.impl.TwilioBasicAuthorizationRequestInterceptor;
import br.com.muttley.notification.twilio.service.impl.TwilioNotificationServiceClient;
import feign.Feign;
import feign.RequestTemplate;
import feign.Target;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 26/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
@ComponentScan(basePackages = "br.com.muttley.notification.twilio.service")
public class TwilioConfig {
/*
    @Bean
    @Primary
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    feign.codec.Encoder feignFormEncoder(@Autowired final ObjectFactory<HttpMessageConverters> messageConverters) {
        return new FormEncoder(new SpringEncoder(messageConverters));
    }
*/

    @Bean
    public TwilioNotificationServiceClient createTwilioNotificationServiceClient(@Value("${muttley.notification.twilio.accountSid}") final String user,
                                                                                 @Value("${muttley.notification.twilio.accountToken}") final String password, @Value("${muttley.notification.twilio.url}") final String url) {
        return Feign.builder()
                .encoder(new FormEncoder())
                .requestInterceptor(new TwilioBasicAuthorizationRequestInterceptor(user, password))
                .target(new Target.HardCodedTarget<>(TwilioNotificationServiceClient.class, url));
    }

    private static class FormEncoder implements Encoder {

        @Override
        public void encode(Object o, Type type, RequestTemplate rt) throws EncodeException {
            if (!(o instanceof Map))
                throw new EncodeException("Can only encode Map data");

            Map m = (Map) o;

            // XXX: quick n dirty!
            StringBuilder sb = new StringBuilder();

            for (Object k : m.keySet()) {
                if (!(k instanceof String))
                    throw new EncodeException("Can only encode String keys");

                if (sb.length() > 0)
                    sb.append("&");

                Object v = m.get(k);
                if (!(v instanceof String))
                    throw new EncodeException("Can only encode String values");

                try {
                    sb.append(URLEncoder.encode((String) k, "UTF-8"))
                            .append("=")
                            .append(URLEncoder.encode((String) v, "UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    throw new EncodeException("Invalid encoding", ex);
                }
            }

            rt.header("Content-Type", "application/x-www-form-urlencoded");
            rt.body(sb.toString());
        }

    }
}
