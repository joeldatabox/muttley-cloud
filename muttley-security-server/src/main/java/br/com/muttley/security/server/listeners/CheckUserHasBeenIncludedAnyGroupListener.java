package br.com.muttley.security.server.listeners;

import br.com.muttley.security.server.events.CheckUserHasBeenIncludedAnyGroupEvent;
import br.com.muttley.security.server.service.UserBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira 23/02/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class CheckUserHasBeenIncludedAnyGroupListener implements ApplicationListener<CheckUserHasBeenIncludedAnyGroupEvent> {
    final UserBaseService service;

    @Autowired
    public CheckUserHasBeenIncludedAnyGroupListener(final UserBaseService service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(final CheckUserHasBeenIncludedAnyGroupEvent event) {
        event.setUserHasBeenIncludedAnyGroup(this.service.hasBeenIncludedAnyGroup(event.getSource()));
    }
}
