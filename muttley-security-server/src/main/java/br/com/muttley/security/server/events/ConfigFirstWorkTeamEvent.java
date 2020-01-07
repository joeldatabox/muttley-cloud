package br.com.muttley.security.server.events;

import br.com.muttley.model.security.User;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 07/01/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ConfigFirstWorkTeamEvent extends ApplicationEvent {
    private final User owner;

    public ConfigFirstWorkTeamEvent(final User owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public User getSource() {
        return this.owner;
    }
}
