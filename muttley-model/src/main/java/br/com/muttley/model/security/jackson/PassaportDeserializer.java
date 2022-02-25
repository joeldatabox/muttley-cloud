package br.com.muttley.model.security.jackson;

import br.com.muttley.model.jackson.converter.DocumentDeserializer;
import br.com.muttley.model.jackson.converter.event.DocumentResolverEvent;
import br.com.muttley.model.security.Passaport;
import br.com.muttley.model.security.events.PassaportResolverEvent;

/**
 * @author Joel Rodrigues Moreira on 07/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class PassaportDeserializer extends DocumentDeserializer<Passaport> {

    @Override
    protected DocumentResolverEvent<Passaport> createEventResolver(String id) {
        return new PassaportResolverEvent(id);
    }

    @Override
    protected Passaport newInstance(String id) {
        return new Passaport().setId(id);
    }
}
