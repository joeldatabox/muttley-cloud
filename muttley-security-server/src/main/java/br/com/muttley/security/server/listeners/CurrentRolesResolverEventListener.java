package br.com.muttley.security.server.listeners;

import br.com.muttley.security.server.events.CurrentRolesResolverEvent;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira 30/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class CurrentRolesResolverEventListener implements ApplicationListener<CurrentRolesResolverEvent> {
    private final WorkTeamService service;

    @Autowired
    public CurrentRolesResolverEventListener(final WorkTeamService service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(final CurrentRolesResolverEvent event) {
        event.setRoles(this.service.loadCurrentRoles(event.getSource().getUser()))
                .setResolved(true);
    }
}
