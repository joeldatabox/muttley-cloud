package br.com.muttley.security.server.events;

import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.User;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira 20/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class CurrentOwnerResolverEvent extends ApplicationEvent {
    private final User user;

    public CurrentOwnerResolverEvent(final User user) {
        super(user);
        this.user = user;
    }

    @Override
    public User getSource() {
        return this.user;
    }

    public CurrentOwnerResolverEvent setOwnerResolved(final Owner owner) {
        this.user.setCurrentOwner(owner);
        return this;
    }

    public CurrentOwnerResolverEvent setOwnerResolved(final OwnerData owner) {
        this.user.setCurrentOwner(owner);
        return this;
    }

}
