package br.com.muttley.security.infra.events;

import br.com.muttley.model.security.model.User;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 08/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Evento lançado toda vez que um usuário se loga no sistema.
 */
public class UserLoggedEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public UserLoggedEvent(final User source) {
        super(source);
    }

    @Override
    public User getSource() {
        return (User) super.getSource();
    }
}
