package br.com.muttley.model.security.jackson;

import br.com.muttley.model.jackson.converter.DocumentDeserializer;
import br.com.muttley.model.jackson.converter.event.DocumentEventResolver;
import br.com.muttley.model.security.Owner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OwnerDeserializer extends DocumentDeserializer<Owner> {

    public OwnerDeserializer(final Class<Owner> clazz, final ObjectMapper mapper, final ApplicationEventPublisher eventPublisher) {
        super(clazz, mapper, eventPublisher);
    }

    @Override
    protected DocumentEventResolver<Owner> createEventResolver(final String id) {
        return new OwnerEventEventResolver(id);
    }

    public class OwnerEventEventResolver extends DocumentEventResolver<Owner> {

        public OwnerEventEventResolver(final String id) {
            super(id);
        }
    }
}
