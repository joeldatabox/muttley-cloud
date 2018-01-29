package br.com.muttley.security.infra.events;

import br.com.muttley.model.security.model.User;
import org.springframework.context.ApplicationEvent;

public class UserCreatedEvent extends ApplicationEvent {
    private final User user;

    public UserCreatedEvent(final User user) {
        super(user);
        this.user = user;
    }

    public final User getUser() {
        return this.user;
    }
}
