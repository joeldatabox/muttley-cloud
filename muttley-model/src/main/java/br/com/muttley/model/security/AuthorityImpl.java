package br.com.muttley.model.security;

import java.util.Objects;

/**
 * @author Joel Rodrigues Moreira on 28/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class AuthorityImpl implements Authority {
    private Role role;
    private String description;

    public AuthorityImpl() {
    }

    public AuthorityImpl(final Role role) {
        this();
        this.role = role;
    }

    public AuthorityImpl(final String role) {
        this(Role.valueOf(role));
    }

    public AuthorityImpl(final Role role, final String description) {
        this(role);
        this.description = description;
    }

    public AuthorityImpl(final String role, final String description) {
        this(role);
        this.description = description;
    }

    @Override
    public Role getRole() {
        return role;
    }

    public AuthorityImpl setRole(final Role role) {
        this.role = role;
        return this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public AuthorityImpl setDescription(final String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if ((!(o instanceof AuthorityImpl)) || (!(o instanceof Authority))) return false;
        final Authority authority = (Authority) o;
        return Objects.equals(role, authority.getRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, 81);
    }
}
