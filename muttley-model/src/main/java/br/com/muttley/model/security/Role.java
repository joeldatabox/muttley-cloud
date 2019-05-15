package br.com.muttley.model.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.TypeAlias;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * @author Joel Rodrigues Moreira on 10/05/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@TypeAlias("role")
public class Role {
    private final String roleName;
    @JsonIgnore
    public static final Role ROLE_OWNER = new Role("ROLE_OWNER");
    @JsonIgnore
    public static final Role ROLE_ROOT = new Role("ROLE_ROOT");
    @JsonIgnore
    protected static final Set<Role> values = new HashSet<>(asList(ROLE_OWNER, ROLE_ROOT));


    @JsonCreator
    public Role(@JsonProperty("roleName") final String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        final Role role = (Role) o;
        return Objects.equals(roleName, role.roleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleName);
    }

    @Override
    public String toString() {
        return roleName;
    }

    @JsonIgnore
    public static final Role valueOf(final String value) {
        return values
                .stream()
                .filter(it -> it.equals(new Role(value)))
                .findAny()
                .orElse(null);
    }
}
