package br.com.muttley.model.security.events;

import br.com.muttley.model.jackson.converter.event.DocumentResolverEvent;
import br.com.muttley.model.security.Owner;

/**
 * @author Joel Rodrigues Moreira on 17/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OwnerResolverEvent extends DocumentResolverEvent<Owner> {
    public OwnerResolverEvent(final String id) {
        super(id);
    }
}
