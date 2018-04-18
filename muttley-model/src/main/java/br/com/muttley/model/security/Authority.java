package br.com.muttley.model.security;

public interface Authority {

    String getName();

    String getDescription();

    /*private String name;
    private String descricao;

    public Authority() {
    }

    public Authority(final String name) {
        this();
        this.name = name;
    }

    public Authority(final Authorities name) {
        this(name.name());
    }

    public String getName() {
        return name;
    }

    public Authority setName(final Authorities name) {
        this.name = name.getDescription();
        return this;
    }

    @JsonIgnore
    @Transient
    public String getDescricao() {
        return this.name;
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
    }*/
}
