package br.com.muttley.security.server.listeners;

import br.com.muttley.model.security.events.ValidateOwnerEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 08/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class ValidateOwnerEventListener implements ApplicationListener<ValidateOwnerEvent> {

    @Override
    public void onApplicationEvent(ValidateOwnerEvent event) {
        event.getSource().setOwner(event.getCurrenteUserFromRequest().getCurrentOwner());
    }
}
