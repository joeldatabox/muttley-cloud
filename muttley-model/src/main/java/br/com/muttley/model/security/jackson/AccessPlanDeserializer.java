package br.com.muttley.model.security.jackson;

import br.com.muttley.model.jackson.converter.DocumentDeserializer;
import br.com.muttley.model.jackson.converter.event.DocumentEventResolver;
import br.com.muttley.model.security.AccessPlan;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class AccessPlanDeserializer extends DocumentDeserializer<AccessPlan> {

    public AccessPlanDeserializer(@Autowired final ObjectMapper mapper, @Autowired final ApplicationEventPublisher eventPublisher) {
        super(mapper, eventPublisher);
    }

    @Override
    protected DocumentEventResolver<AccessPlan> createEventResolver(final String id) {
        return new AccessPlanEventResolver(id);
    }

    public class AccessPlanEventResolver extends DocumentEventResolver<AccessPlan> {

        public AccessPlanEventResolver(final String id) {
            super(id);
        }
    }
}
