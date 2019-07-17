package br.com.muttley.model.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.TypeAlias;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;

/**
 * @author Joel Rodrigues Moreira on 16/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@TypeAlias("role")
@EqualsAndHashCode(of = "roleName")
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
        this.roleName = roleName.toUpperCase();
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public String toString() {
        return roleName;
    }

    /**
     * Com base na String passada por parametro,
     * o metodo retornará uma instancia {@link Role} caso a mesma seja válida
     *
     * @param value -> nameRole a ser procurado
     * @return {@link Role} caso seja encontrado
     */
    @JsonIgnore
    public static final Role valueOf(final String value) {
        return values
                .stream()
                .filter(it -> it.getRoleName().equals(value))
                .findAny()
                .orElse(null);
    }

    /**
     * Com base nas String's passadas por parametro,
     * o metodo retornará um array de instancias de {@link Role} caso possua alguma String válidaseja válida
     *
     * @param values -> vetor com nameRole a ser procurados
     * @return {@link Role[]} caso seja encontrado
     */
    @JsonIgnore
    public static final Role[] valueOf(final String... values) {
        final Object[] roles = Role.values
                .stream()
                .filter(it -> {
                    for (final String v : values) {
                        if (it.getRoleName().equals(v)) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(toSet())
                .toArray();
        if (roles == null || roles.length == 0) {
            return null;
        }
        return (Role[]) roles;
    }

    /**
     * Com base na String's passada por parametro,
     * o metodo retorna uma roleName padronizada
     * <br>
     * Exemplo: role = "Notas", endNameRole = read
     * resultado = ROLE_NOTAS_READ
     *
     * @param role        -> nome básico da role;
     * @param endNameRole -> sufixo da role;
     * @return {@link String}
     */
    @JsonIgnore
    public static final String toPatternRole(final String endNameRole, final String role) {
        final StringBuilder sbuilder = new StringBuilder();
        if (!role.startsWith("ROLE_")) {
            sbuilder.append("ROLE_").append(role);
        }
        if (!role.endsWith("_" + endNameRole)) {
            sbuilder.append("_").append(endNameRole);
        }
        return sbuilder.toString().toUpperCase();
    }

    /**
     * Com base nas String's passadas por parametro,
     * o metodo retorna as roleNames padronizadas
     * <br>
     * Exemplo: role = "Notas", endNameRole = read
     * resultado = [ROLE_NOTAS_READ]
     *
     * @param roles       -> nomes básicos de role;
     * @param endNameRole -> sufixo das role;
     * @return {@link String}
     */
    @JsonIgnore
    public static final String[] toPatternRole(final String endNameRole, final String... roles) {
        if (roles == null) return null;
        return of(roles)
                .map(r -> Role.toPatternRole(endNameRole, r))
                .toArray(String[]::new);
    }
}
