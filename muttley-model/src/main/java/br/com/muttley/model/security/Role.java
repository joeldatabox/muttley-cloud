package br.com.muttley.model.security;

import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.TypeAlias;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * @author Joel Rodrigues Moreira on 16/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@TypeAlias("role")
@EqualsAndHashCode(of = "roleName")
public class Role {
    private final String roleName;

    public static final Role ROLE_OWNER = new Role("ROLE_OWNER");
    public static final Role ROLE_ROOT = new Role("ROLE_ROOT");

    protected static final Set<Role> values = new HashSet<>(asList(ROLE_OWNER, ROLE_ROOT));


    public Role(final String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return roleName;
    }

    public static final Role valueOf(final String value) {
        return values
                .stream()
                .filter(it -> it.equals(new Role(value)))
                .findAny()
                .orElse(null);
    }
}
