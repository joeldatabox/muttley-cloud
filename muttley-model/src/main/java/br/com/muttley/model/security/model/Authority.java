package br.com.muttley.model.security.model;

import br.com.muttley.model.security.model.enumeration.Authorities;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;
import java.beans.Transient;
import java.util.Objects;

public class Authority {

    @NotNull
    private Authorities name;

    public Authority() {
    }

    public Authority(final Authorities name) {
        this();
        this.name = name;
    }

    public Authorities getName() {
        return name;
    }

    public Authority setName(final Authorities name) {
        this.name = name;
        return this;
    }

    @JsonIgnore
    @Transient
    public String getDescricao() {
        return this.name.getDescription();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Authority)) return false;
        final Authority authority = (Authority) o;
        return name == authority.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, 87);
    }
}
