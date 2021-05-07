package br.com.muttley.model.security.jackson;

import br.com.muttley.model.jackson.converter.DocumentDeserializer;
import br.com.muttley.model.jackson.converter.event.DocumentResolverEvent;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.model.security.events.WorkTeamResolverEvent;

/**
 * @author Joel Rodrigues Moreira on 07/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class WorkTeamDeserializer extends DocumentDeserializer<WorkTeam> {

    @Override
    protected DocumentResolverEvent<WorkTeam> createEventResolver(String id) {
        return new WorkTeamResolverEvent(id);
    }

    @Override
    protected WorkTeam newInstance(String id) {
        return new WorkTeam().setId(id);
    }
}
