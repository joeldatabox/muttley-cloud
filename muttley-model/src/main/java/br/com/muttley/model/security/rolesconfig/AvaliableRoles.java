package br.com.muttley.model.security.rolesconfig;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * @author Joel Rodrigues Moreira on 18/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Accessors(chain = true)
public class AvaliableRoles {
    private final Set<ViewRoleDefinition> viewRoleDefinitions;

    public AvaliableRoles(final Collection<ViewRoleDefinition> viewRoleDefinitions) {
        this.viewRoleDefinitions = new HashSet<>(viewRoleDefinitions);
    }

    public AvaliableRoles(final ViewRoleDefinition... viewRoleDefinitions) {
        this(asList(viewRoleDefinitions));
    }

}
