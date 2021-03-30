package br.com.muttley.security.server.listeners;

import br.com.muttley.security.server.events.CurrentDatabindingResolverEvent;
import br.com.muttley.security.server.service.UserDataBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira 30/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class CurrentDatabindingResolverEventListener implements ApplicationListener<CurrentDatabindingResolverEvent> {
    private final UserDataBindingService service;

    @Autowired
    public CurrentDatabindingResolverEventListener(final UserDataBindingService service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(final CurrentDatabindingResolverEvent event) {
        event.setDataBindings(this.service.listBy(event.getSource().getUser()))
                .setResolved(true);
    }
}
