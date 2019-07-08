package br.com.muttley.model.security.rolesconfig;

import br.com.muttley.model.security.Role;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Joel Rodrigues Moreira on 02/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@EqualsAndHashCode(of = "typeRole")
public class RoleDefinition {
    private final int id;
    private final Role typeRole;
    private final String description;

    protected RoleDefinition(final int id, final Role typeRole, final String description) {
        this.id = id;
        this.typeRole = typeRole;
        this.description = description;
    }

    protected RoleDefinition(final int id, final Role typeRole) {
        this(id, typeRole, null);
    }
}
