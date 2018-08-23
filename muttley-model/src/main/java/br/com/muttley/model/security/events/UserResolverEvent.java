package br.com.muttley.model.security.events;

import br.com.muttley.model.security.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Accessors(chain = true)
public class UserResolverEvent extends ApplicationEvent {
    @Getter
    final String email;
    @Getter
    @Setter
    protected User userResolved;

    public UserResolverEvent(final String email) {
        super(email);
        this.email = email;
    }

    public boolean isResolved() {
        return this.userResolved != null;
    }
}
