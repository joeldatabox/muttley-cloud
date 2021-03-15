package br.com.muttley.model.security.events;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira 12/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class UserResolverFromJWTEvent extends ApplicationEvent {
    protected final JwtToken token;
    @Getter
    @Setter
    protected User userResolved;

    public UserResolverFromJWTEvent(final JwtToken token) {
        super(token);
        this.token = token;
    }

    @Override
    public JwtToken getSource() {
        return this.token;
    }

    public boolean isResolved() {
        return this.userResolved != null;
    }
}
