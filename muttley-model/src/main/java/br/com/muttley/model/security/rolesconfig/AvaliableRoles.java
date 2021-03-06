package br.com.muttley.model.security.rolesconfig;

import br.com.muttley.model.security.Role;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static br.com.muttley.model.security.rolesconfig.ViewRoleDefinition.newRoleDefinition;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;


/**
 * @author Joel Rodrigues Moreira on 18/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Accessors(chain = true)
public class AvaliableRoles {
    private static int idsCounter = 0;
    private final Set<ViewRoleDefinition> viewRoleDefinitions;

    protected AvaliableRoles(final Collection<ViewRoleDefinition> viewRoleDefinitions) {
        this.viewRoleDefinitions = new HashSet<>(viewRoleDefinitions);
    }

    protected AvaliableRoles(final ViewRoleDefinition... viewRoleDefinitions) {
        this(asList(viewRoleDefinitions));
    }

    public Set<Role> getDependenciesRolesFrom(final Set<Role> currentRoles) {
        return currentRoles.stream().map(it -> getDependenciesRolesFrom(it))
                .reduce((acc, roles) -> {
                    acc.addAll(roles);
                    return acc;
                })
                .orElse(new HashSet<>(0));

    }

    public Set<Role> getDependenciesRolesFrom(final Role role) {
        return this.viewRoleDefinitions.stream()
                .filter(it -> it.contains(role))
                .map(it -> it.getDependencies())
                .reduce((acc, roles) -> {
                    acc.addAll(roles);
                    return acc;
                }).orElse(new HashSet<>(0));
    }

    public static ViewRoleDefinition newViewRoleDefinition(final String title, final String description, final RoleDefinition... roleDefinitions) {
        idsCounter++;
        return new ViewRoleDefinition(idsCounter, null, title, description, roleDefinitions);
    }

    public static ViewRoleDefinition newViewRoleDefinition(final String title, final String description, final Role... roles) {
        idsCounter++;
        return new ViewRoleDefinition(idsCounter, null, title, description,
                stream(roles).map(it -> newRoleDefinition(it)).toArray(RoleDefinition[]::new)
        );
    }

    public static ViewRoleDefinition newViewRoleDefinition(final IconMenu iconMenu, final String title, final String description, final Role... roles) {
        idsCounter++;
        return new ViewRoleDefinition(idsCounter, iconMenu, title, description,
                stream(roles).map(it -> newRoleDefinition(it)).toArray(RoleDefinition[]::new)
        );
    }

    public static ViewRoleDefinition newViewRoleDefinition(final String icon, final String title, final String description, final Role... roles) {
        idsCounter++;
        final IconMenu iconMenu = new IconMenu(icon);
        return new ViewRoleDefinition(idsCounter, iconMenu, title, description,
                stream(roles).map(it -> newRoleDefinition(it)).toArray(RoleDefinition[]::new)
        );
    }

    public static ViewRoleDefinition newViewRoleDefinition(final String icon, final String title, final String description, final RoleDefinition... roleDefinitions) {
        idsCounter++;
        final IconMenu iconMenu = new IconMenu(icon);
        return new ViewRoleDefinition(idsCounter, iconMenu, title, description, roleDefinitions);
    }

    public static ViewRoleDefinition newViewRoleDefinition(final String icon, final FontSet fontSet, final String title, final String description, final Role... roles) {
        idsCounter++;
        final IconMenu iconMenu = new IconMenu(icon, fontSet);
        return new ViewRoleDefinition(idsCounter, iconMenu, title, description,
                stream(roles).map(it -> newRoleDefinition(it)).toArray(RoleDefinition[]::new)
        );
    }

    public static ViewRoleDefinition newViewRoleDefinition(final String icon, final FontSet fontSet, final String title, final String description, final RoleDefinition... roleDefinitions) {
        idsCounter++;
        final IconMenu iconMenu = new IconMenu(icon, fontSet);
        return new ViewRoleDefinition(idsCounter, iconMenu, title, description, roleDefinitions);
    }


    public static ViewRoleDefinition newViewRoleDefinition(final String title, final String description) {
        idsCounter++;
        return new ViewRoleDefinition(idsCounter, null, title, description, (RoleDefinition) null);
    }

    public static AvaliableRoles newAvaliableRoles(final Collection<ViewRoleDefinition> viewRoleDefinitions) {
        idsCounter = 0;
        return new AvaliableRoles(viewRoleDefinitions);
    }

    public static AvaliableRoles newAvaliableRoles(final ViewRoleDefinition... viewRoleDefinitions) {
        idsCounter = 0;
        ViewRoleDefinition.idsCounter = 0;
        return new AvaliableRoles(viewRoleDefinitions);
    }
}
