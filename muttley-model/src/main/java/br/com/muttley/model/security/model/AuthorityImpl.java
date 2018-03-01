package br.com.muttley.model.security.model;

import java.util.Objects;

/**
 * @author Joel Rodrigues Moreira on 28/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class AuthorityImpl implements Authority {
    private String name;
    private String description;

    public AuthorityImpl() {
    }

    public AuthorityImpl(final String name) {
        this();
        this.name = name;
    }

    public AuthorityImpl(final String name, final String description) {
        this(name);
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    public AuthorityImpl setName(final String name) {
        this.name = name;
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
        return Objects.equals(name, authority.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, 81);
    }
}
