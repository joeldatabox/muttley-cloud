package br.com.muttley.model.events;

import br.com.muttley.model.security.Owner;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 16/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OwnerCreateEvent extends ApplicationEvent {
    private final Owner owner;

    public OwnerCreateEvent(final Owner owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public Owner getSource() {
        return this.owner;
    }
}
