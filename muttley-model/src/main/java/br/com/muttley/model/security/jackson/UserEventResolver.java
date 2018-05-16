package br.com.muttley.model.security.jackson;

import br.com.muttley.model.security.User;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class UserEventResolver extends ApplicationEvent {
    final String email;
    protected User valueResolved;

    public UserEventResolver(final String email) {
        super(email);
        this.email = email;
    }

    public User getUserResolver() {
        return valueResolved;
    }

    public UserEventResolver setValueResolved(final User valueResolved) {
        this.valueResolved = valueResolved;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public boolean isResolved() {
        return this.valueResolved != null;
    }
}
