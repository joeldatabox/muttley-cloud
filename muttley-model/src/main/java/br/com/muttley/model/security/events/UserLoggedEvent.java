package br.com.muttley.model.security.events;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 08/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Evento lançado toda vez que um usuário se loga no sistema.
 */
public class UserLoggedEvent extends ApplicationEvent {
    @Getter
    private final User user;
    @Getter
    private final JwtToken token;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public UserLoggedEvent(final JwtToken token, final User source) {
        super(source);
        this.token = token;
        this.user = source;
    }

    @Override
    public User getSource() {
        return user;
    }
}
