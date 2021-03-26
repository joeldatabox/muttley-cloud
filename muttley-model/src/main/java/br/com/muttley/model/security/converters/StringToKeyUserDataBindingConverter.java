package br.com.muttley.model.security.converters;

import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.model.security.KeyUserDataBinding;
import br.com.muttley.model.security.KeyUserDataBindingAvaliable;
import br.com.muttley.model.security.events.KeyUserDataBindingResolverEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Joel Rodrigues Moreira 10/02/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class StringToKeyUserDataBindingConverter implements Converter<String, KeyUserDataBinding> {
    private final ApplicationEventPublisher publisher;

    @Autowired
    public StringToKeyUserDataBindingConverter(final ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public KeyUserDataBinding convert(final String source) {
        if (source == null) {
            return null;
        }
        final KeyUserDataBindingResolverEvent event = new KeyUserDataBindingResolverEvent(source);
        this.publisher.publishEvent(event);
        if (!event.isResolved()) {
            final KeyUserDataBinding result = KeyUserDataBindingAvaliable.from(source);
            if (result == null) {
                throw new MuttleyException("Crie um listener pra resolver isso");
            }
            return result;
        }
        return event.getResolvedValue();
    }
}
