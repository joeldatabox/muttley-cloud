package br.com.muttley.model.security.rolesconfig;

import br.com.muttley.model.security.Role;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

/**
 * @author Joel Rodrigues Moreira on 02/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@EqualsAndHashCode(of = "title")
public class ViewRoleDefinition {
    private final String title;
    private final String description;
    private final Set<RoleDefinition> roleDefinitions;

    public ViewRoleDefinition(final String title, final String description, final RoleDefinition... roleDefinitions) {
        this.title = title;
        this.description = description;
        if (roleDefinitions == null) {
            this.roleDefinitions = new HashSet<>();
        } else {
            this.roleDefinitions = new HashSet<>(asList(roleDefinitions));
        }
    }

    public ViewRoleDefinition(final String title, final String description, final Role... roles) {
        this(title, description, stream(roles).map(it -> new RoleDefinition(it)).toArray(RoleDefinition[]::new));
    }

    public ViewRoleDefinition(final String title, final String description) {
        this(title, description, (RoleDefinition) null);
    }

    public ViewRoleDefinition add(RoleDefinition roleDefinition) {
        this.roleDefinitions.add(roleDefinition);
        return this;
    }

    public ViewRoleDefinition add(RoleDefinition... roleDefinitions) {
        this.roleDefinitions.addAll(asList(roleDefinitions));
        return this;
    }

    public ViewRoleDefinition add(Collection<RoleDefinition> roleDefinitions) {
        this.roleDefinitions.addAll(roleDefinitions);
        return this;
    }
}
