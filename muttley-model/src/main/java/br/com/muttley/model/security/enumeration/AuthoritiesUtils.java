package br.com.muttley.model.security.enumeration;

import br.com.muttley.model.security.Authority;
import br.com.muttley.model.security.AuthorityImpl;
import br.com.muttley.model.security.Role;

import java.util.HashSet;
import java.util.Set;

import static br.com.muttley.model.security.Role.ROLE_OWNER;
import static java.util.Arrays.asList;

/**
 * @author Joel Rodrigues Moreira on 16/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class AuthoritiesUtils {
    protected static final Set<Authority> authorities = new HashSet<>(asList(new AuthorityImpl(ROLE_OWNER, "Permissões de dono da base de dados")));

    public static Authority getAuthority(final Role role) {
        return getAllAuthorities()
                .stream()
                .filter(it -> it.getRole().equals(role))
                .findAny()
                .orElse(null);
    }

    public static Set<Authority> getAllAuthorities() {
        if (authorities != null) {
            return authorities;
        }
        return authorities;
    }
}
