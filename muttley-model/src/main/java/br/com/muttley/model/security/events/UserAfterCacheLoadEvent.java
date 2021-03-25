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
 * Evento lançado toda vez que um usuário é recuperado no cache do redis.
 * Deve-se utilizar esse evento para saber a hora certa de se carregar detalhes como,
 * preferencias de usuário;
 */
public class UserAfterCacheLoadEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public UserAfterCacheLoadEvent(final JwtToken token, final User user) {
        super(new UserAfterCacheLoadEventSource(token, user));
    }

    @Override
    public UserAfterCacheLoadEventSource getSource() {
        return (UserAfterCacheLoadEventSource) super.getSource();
    }

    public User getUser() {
        return this.getSource().getUser();
    }

    public JwtToken getJwtToken() {
        return this.getSource().getToken();
    }

    @Getter
    private static class UserAfterCacheLoadEventSource {
        private final JwtToken token;
        private final User user;

        public UserAfterCacheLoadEventSource(final JwtToken token, final User user) {
            this.token = token;
            this.user = user;
        }
    }
}
