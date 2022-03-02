package br.com.muttley.model.security.events;

import br.com.muttley.model.jackson.converter.event.ModelResolverEvent;
import br.com.muttley.model.security.Passaport;

/**
 * @author Joel Rodrigues Moreira on 07/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class PassaportResolverEvent extends ModelResolverEvent<Passaport> {
    public PassaportResolverEvent(String id) {
        super(id);
    }
}
