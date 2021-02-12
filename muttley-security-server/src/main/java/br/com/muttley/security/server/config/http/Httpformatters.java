package br.com.muttley.security.server.config.http;

import br.com.muttley.model.security.KeyUserDataBinding;
import br.com.muttley.model.security.events.KeyUserDataBindingResolverEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

/**
 * @author Joel Rodrigues Moreira 12/02/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
public class Httpformatters {

    private final ApplicationEventPublisher publisher;

    @Autowired
    public Httpformatters(final ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Bean
    public Formatter<KeyUserDataBinding> localDateFormatter() {
        return new Formatter<KeyUserDataBinding>() {

            @Override
            public String print(final KeyUserDataBinding key, final Locale locale) {
                return key.getKey();
            }

            @Override
            public KeyUserDataBinding parse(final String text, final Locale locale) throws ParseException {
                final KeyUserDataBindingResolverEvent event = new KeyUserDataBindingResolverEvent(text);
                publisher.publishEvent(event);
                return event.getResolvedValue();
            }
        };
    }
}
