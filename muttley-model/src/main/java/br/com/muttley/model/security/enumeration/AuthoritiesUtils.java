package br.com.muttley.model.security.enumeration;

import br.com.muttley.model.security.Authority;
import br.com.muttley.model.security.AuthorityImpl;
import br.com.muttley.model.security.Role;

import java.util.HashSet;
import java.util.Set;

import static br.com.muttley.model.security.Role.ROLE_OWNER;
import static br.com.muttley.model.security.Role.ROLE_ROOT;
import static java.util.Arrays.asList;

/**
 * @author Joel Rodrigues Moreira on 16/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class AuthoritiesUtils {
    public static final Authority AUTHORITY_ROLE_OWNER = new AuthorityImpl(ROLE_OWNER, "Permissões de dono da base de dados");
    public static final Authority AUTHORITY_ROLE_ROOT = new AuthorityImpl(ROLE_ROOT, "Permissões de root do sistema");

    protected static final Set<Authority> authorities = new HashSet<Authority>(asList(
            AUTHORITY_ROLE_OWNER,
            AUTHORITY_ROLE_ROOT
    ));

    public static Authority getAuthority(final Role role) {
        return getAllAuthorities()
                .parallelStream()
                .filter(it -> it.getRole().equals(role))
                .findAny()
                .orElse(null);
    }

    public static Set<Authority> getAllAuthorities() {
        return authorities;
    }
}
