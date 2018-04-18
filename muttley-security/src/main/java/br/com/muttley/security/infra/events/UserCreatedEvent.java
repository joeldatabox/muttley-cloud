package br.com.muttley.security.infra.events;

import br.com.muttley.model.security.User;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 08/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Evento disparado toda vez que é criado um usuário no sistema
 */
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
