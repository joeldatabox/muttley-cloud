package br.com.muttley.model.security.jackson;

import br.com.muttley.model.jackson.converter.DocumentDeserializer;
import br.com.muttley.model.jackson.converter.event.DocumentResolverEvent;
import br.com.muttley.model.security.Owner;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class OwnerDeserializer extends DocumentDeserializer<Owner> {

    @Override
    protected DocumentResolverEvent<Owner> createEventResolver(final String id) {
        return new OwnerEventResolver(id);
    }

    @Override
    protected Owner newInstance(final String id) {
        return new Owner().setId(id);
    }

    public class OwnerEventResolver extends DocumentResolverEvent<Owner> {
        public OwnerEventResolver(final String id) {
            super(id);
        }
    }

}
