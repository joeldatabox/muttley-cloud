package br.com.muttley.model.security.events;

import br.com.muttley.model.security.User;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class UserResolverEvent extends ApplicationEvent {
    final String userName;
    protected User valueResolved;

    public UserResolverEvent(final String userName) {
        super(userName);
        this.userName = userName;
    }

    public User getUserResolver() {
        return valueResolved;
    }

    public UserResolverEvent setValueResolved(final User valueResolved) {
        this.valueResolved = valueResolved;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isResolved() {
        return this.valueResolved != null;
    }
}
