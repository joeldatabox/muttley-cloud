package br.com.muttley.security.server.listeners;

import br.com.muttley.security.server.events.CurrentPreferencesResolverEvent;
import br.com.muttley.security.server.service.UserPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira 30/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class CurrentPreferencesResolverEventListener implements ApplicationListener<CurrentPreferencesResolverEvent> {

    private final UserPreferencesService service;

    @Autowired
    public CurrentPreferencesResolverEventListener(final UserPreferencesService service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(final CurrentPreferencesResolverEvent event) {
        event.setUserPreferences(this.service.getUserPreferences(event.getSource().getUser()))
                .setResolved(true);
    }
}
