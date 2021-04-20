package br.com.muttley.muttleyadminserver.events;

import br.com.muttley.model.security.Owner;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira 20/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OwnerCreatedEvent extends ApplicationEvent {
    private final Owner owner;

    public OwnerCreatedEvent(final Owner owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public Owner getSource() {
        return this.owner;
    }
}
