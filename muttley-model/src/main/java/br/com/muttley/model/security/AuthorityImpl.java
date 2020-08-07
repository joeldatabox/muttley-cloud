package br.com.muttley.model.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@EqualsAndHashCode(of = {"role"})
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

    @JsonCreator
    public AuthorityImpl(@JsonProperty("role") final Role role, @JsonProperty("description") final String description) {
        this(role);
        this.description = description;
    }

    public AuthorityImpl(final String role, final String description) {
        this(role);
        this.description = description;
    }
}
