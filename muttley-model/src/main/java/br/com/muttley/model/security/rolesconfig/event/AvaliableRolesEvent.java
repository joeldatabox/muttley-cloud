package br.com.muttley.model.security.rolesconfig.event;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.rolesconfig.AvaliableRoles;
import br.com.muttley.model.security.rolesconfig.ViewRoleDefinition;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import static java.util.Arrays.asList;

/**
 * @author Joel Rodrigues Moreira on 02/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
public class AvaliableRolesEvent extends ApplicationEvent {
    private final User user;
    private final AvaliableRoles source;

    public AvaliableRolesEvent(final User user, final AvaliableRoles source) {
        super(source);
        this.user = user;
        this.source = source;
    }

    public AvaliableRolesEvent add(final ViewRoleDefinition... viewRoleDefinitions) {
        this.source.getViewRoleDefinitions().addAll(asList(viewRoleDefinitions));
        return this;
    }

    @Override
    public AvaliableRoles getSource() {
        return this.source;
    }


}
