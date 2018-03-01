package br.com.muttley.model.security.model.enumeration;


import br.com.muttley.model.security.model.Authority;

public enum Authorities implements Authority {
    ROLE_USER("Permissão simples de usuário"),
    ROLE_ADMIN("Permissão de administrador");

    private final String description;

    Authorities(final String description) {
        this.description = description;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
