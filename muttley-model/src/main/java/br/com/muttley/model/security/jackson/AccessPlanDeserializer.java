package br.com.muttley.model.security.jackson;

import br.com.muttley.model.jackson.converter.DocumentDeserializer;
import br.com.muttley.model.jackson.converter.event.DocumentResolverEvent;
import br.com.muttley.model.security.AccessPlan;
import br.com.muttley.model.security.events.AccessPlanResolver;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class AccessPlanDeserializer extends DocumentDeserializer<AccessPlan> {

    @Override
    protected DocumentResolverEvent<AccessPlan> createEventResolver(final String id) {
        return new AccessPlanResolver(id);
    }

    @Override
    protected AccessPlan newInstance(final String id) {
        return new AccessPlan().setId(id);
    }
}
