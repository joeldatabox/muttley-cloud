package br.com.muttley.model.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Joel Rodrigues Moreira on 28/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = {"name"})
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
}
