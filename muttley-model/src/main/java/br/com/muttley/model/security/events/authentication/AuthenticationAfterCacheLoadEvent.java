package br.com.muttley.model.security.events.authentication;

import br.com.muttley.model.security.XAPIToken;
import br.com.muttley.model.security.authentication.Authentication;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 19/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Evento lançado toda vez que um usuário é recuperado no cache do redis.
 * Deve-se utilizar esse evento para saber a hora certa de se carregar detalhes como,
 * preferencias de usuário;
 */
public class AuthenticationAfterCacheLoadEvent extends ApplicationEvent {

    public AuthenticationAfterCacheLoadEvent(final XAPIToken token, final Authentication authentication) {
        super(new AuthenticationAfterCacheLoadEventSource(token, authentication));
    }

    @Override
    public AuthenticationAfterCacheLoadEventSource getSource() {
        return (AuthenticationAfterCacheLoadEventSource) super.getSource();
    }

    public XAPIToken getToken() {
        return this.getSource().getXAPIToken();
    }

    public Authentication getAuthentication() {
        return this.getSource().getAuthentication();
    }

    @Getter
    private static class AuthenticationAfterCacheLoadEventSource {
        private final XAPIToken XAPIToken;
        private final Authentication authentication;

        private AuthenticationAfterCacheLoadEventSource(XAPIToken XAPIToken, Authentication authentication) {
            this.XAPIToken = XAPIToken;
            this.authentication = authentication;
        }
    }
}
