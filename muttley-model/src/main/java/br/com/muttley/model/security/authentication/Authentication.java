package br.com.muttley.model.security.authentication;

import br.com.muttley.model.security.Authority;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserDataBinding;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.model.workteam.WorkTeamDomain;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 19/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Authentication {
    User getCurrentUser();

    Authentication setCurrentUser(final User user);

    Owner getCurrentOwner();

    Authentication setCurrentOwner(final Owner currentOwner);

    Set<Authority> getAuthorities();

    Authentication setAuthorities(final Set<Authority> authorities);

    Authentication setAuthorities(final Collection<Role> roles);

    WorkTeamDomain getWorkTeam();

    Authentication setWorkTeam(final WorkTeamDomain domain);

    UserPreferences getPreferences();

    Authentication setPreferences(final UserPreferences preferences);

    List<UserDataBinding> getDataBindings();

    Authentication setDataBindings(final List<UserDataBinding> dataBindings);

    boolean isOdiUser();

    Authentication setOdinUser(final boolean odinUser);

}
