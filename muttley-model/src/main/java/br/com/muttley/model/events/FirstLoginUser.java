package br.com.muttley.model.events;

import br.com.muttley.model.security.User;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 11/10/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class FirstLoginUser extends ApplicationEvent {
    final User user;

    public FirstLoginUser(final User user) {
        super(user);
        this.user = user;
    }

    @Override
    public User getSource() {
        return this.user;
    }
}
