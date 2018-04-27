package br.com.muttley.model.security.events;

import br.com.muttley.model.jackson.converter.event.DocumentEventResolver;
import br.com.muttley.model.security.AccessPlan;

/**
 * @author Joel Rodrigues Moreira on 17/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class AccessPlanResolver extends DocumentEventResolver<AccessPlan> {
    public AccessPlanResolver(final String id) {
        super(id);
    }
}
