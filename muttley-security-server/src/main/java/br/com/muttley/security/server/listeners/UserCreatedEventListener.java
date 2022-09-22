package br.com.muttley.security.server.listeners;

import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.events.UserCreatedEvent;
import br.com.muttley.security.server.events.AccessPlanDefaultEvent;
import br.com.muttley.security.server.service.NoSecurityOwnerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 21/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class UserCreatedEventListener implements ApplicationListener<UserCreatedEvent> {
    private final boolean autoCreateOwner;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final NoSecurityOwnerService ownerService;

    public UserCreatedEventListener(@Value("${muttley.security-server.owner.autoCreateBefore:false}") boolean autoCreateOwner, final ApplicationEventPublisher applicationEventPublisher, NoSecurityOwnerService ownerService) {
        this.autoCreateOwner = autoCreateOwner;
        this.applicationEventPublisher = applicationEventPublisher;
        this.ownerService = ownerService;
    }

    @Override
    public void onApplicationEvent(UserCreatedEvent userCreatedEvent) {
        if (this.autoCreateOwner) {
            final AccessPlanDefaultEvent event = new AccessPlanDefaultEvent("");
            this.applicationEventPublisher.publishEvent(event);
            final Owner owner = new Owner()
                    .setUserMaster(userCreatedEvent.getUser())
                    .setAccessPlan(event.getResolved())
                    .setName("Meus dados(" + userCreatedEvent.getUser().getName() + ")");
            this.ownerService.save(owner);

        }

    }
}
