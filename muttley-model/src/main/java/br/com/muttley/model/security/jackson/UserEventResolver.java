package br.com.muttley.model.security.jackson;

import br.com.muttley.model.security.User;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class UserEventResolver extends ApplicationEvent {
    final String id;
    protected User valueResolved;

    public UserEventResolver(final String id) {
        super(id);
        this.id = id;
    }

    public User getUserResolver() {
        return valueResolved;
    }

    public UserEventResolver setValueResolved(final User valueResolved) {
        this.valueResolved = valueResolved;
        return this;
    }

    public String getId() {
        return id;
    }
}
