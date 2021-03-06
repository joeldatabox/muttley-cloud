package br.com.muttley.model.security.rolesconfig;

import br.com.muttley.model.security.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

/**
 * @author Joel Rodrigues Moreira on 18/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@EqualsAndHashCode(of = "title")
public class ViewRoleDefinition {
    @JsonIgnore
    protected static int idsCounter = 0;
    private final int id;
    private final IconMenu icon;
    private final String title;
    private final String description;
    private final Set<RoleDefinition> roleDefinitions;
    private final Set<Role> dependencies = new HashSet<>();

    protected ViewRoleDefinition(final int id, final IconMenu icon, final String title, final String description, final RoleDefinition... roleDefinitions) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.description = description;
        if (roleDefinitions == null) {
            this.roleDefinitions = new HashSet<>();
        } else {
            this.roleDefinitions = new HashSet<>(asList(roleDefinitions));
        }
    }

    protected ViewRoleDefinition(final int id, final IconMenu icon, final String title, final String description, final Role... roles) {
        this(id, icon, title, description, stream(roles).map(it -> {
            idsCounter++;
            return new RoleDefinition(idsCounter, it);
        }).toArray(RoleDefinition[]::new));
    }

    protected ViewRoleDefinition(final int id, final IconMenu icon, final String title, final String description) {
        this(id, icon, title, description, (RoleDefinition) null);
    }

    protected ViewRoleDefinition add(RoleDefinition roleDefinition) {
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

    public ViewRoleDefinition addDependencies(final Role... dependencies) {
        this.dependencies.addAll(asList(dependencies));
        return this;
    }

    public boolean contains(final RoleDefinition role) {
        return this.roleDefinitions.contains(role);
    }

    public boolean contains(final Role role) {
        return this.contains(new RoleDefinition(0, role));
    }

    public static RoleDefinition newRoleDefinition(final Role typeRole, final String description) {
        idsCounter++;
        return new RoleDefinition(idsCounter, typeRole, description);
    }

    public static RoleDefinition newRoleDefinition(final Role typeRole) {
        idsCounter++;
        return new RoleDefinition(idsCounter, typeRole);
    }
}
