package br.com.muttley.localcache.services;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira 25/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface LocalRolesService {
    public static final String BASIC_KEY = "ROLES:";

    Set<Role> loadCurrentRoles(final JwtToken token, final User user);

    void expireRoles(final User user);

}
