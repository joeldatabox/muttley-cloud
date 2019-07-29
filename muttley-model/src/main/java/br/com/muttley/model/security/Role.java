package br.com.muttley.model.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.TypeAlias;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Joel Rodrigues Moreira on 10/05/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@TypeAlias("role")
public class Role {
    private final String roleName;

    @JsonIgnore
    protected static final Set<Role> values = new HashSet<>();

    @JsonIgnore
    public static final Role ROLE_OWNER = new Role("ROLE_OWNER");
    @JsonIgnore
    public static final Role ROLE_ROOT = new Role("ROLE_ROOT");

    @JsonIgnore
    public static final Role ROLE_ACCESS_PLAN_CREATE = new Role("ROLE_ACCESS_PLAN_CREATE");
    @JsonIgnore
    public static final Role ROLE_ACCESS_PLAN_READ = new Role("ROLE_ACCESS_PLAN_READ");
    @JsonIgnore
    public static final Role ROLE_ACCESS_PLAN_UPDATE = new Role("ROLE_ACCESS_PLAN_UPDATE");
    @JsonIgnore
    public static final Role ROLE_ACCESS_PLAN_DELETE = new Role("ROLE_ACCESS_PLAN_DELETE");
    @JsonIgnore
    public static final Role ROLE_ACCESS_PLAN_SIMPLE_USE = new Role("ROLE_ACCESS_PLAN_SIMPLE_USE");

    @JsonIgnore
    public static final Role ROLE_MOBILE_ACCESS_PLAN_CREATE = new Role("ROLE_MOBILE_ACCESS_PLAN_CREATE");
    @JsonIgnore
    public static final Role ROLE_MOBILE_ACCESS_PLAN_READ = new Role("ROLE_MOBILE_ACCESS_PLAN_READ");
    @JsonIgnore
    public static final Role ROLE_MOBILE_ACCESS_PLAN_UPDATE = new Role("ROLE_MOBILE_ACCESS_PLAN_UPDATE");
    @JsonIgnore
    public static final Role ROLE_MOBILE_ACCESS_PLAN_DELETE = new Role("ROLE_MOBILE_ACCESS_PLAN_DELETE");

    @JsonIgnore
    public static final Role ROLE_OWNER_CREATE = new Role("ROLE_OWNER_CREATE");
    @JsonIgnore
    public static final Role ROLE_OWNER_READ = new Role("ROLE_OWNER_READ");
    @JsonIgnore
    public static final Role ROLE_OWNER_UPDATE = new Role("ROLE_OWNER_UPDATE");
    @JsonIgnore
    public static final Role ROLE_OWNER_DELETE = new Role("ROLE_OWNER_DELETE");
    @JsonIgnore
    public static final Role ROLE_OWNER_SIMPLE_USE = new Role("ROLE_OWNER_SIMPLE_USE");

    @JsonIgnore
    public static final Role ROLE_USER_VIEW_CREATE = new Role("ROLE_USER_VIEW_CREATE");
    @JsonIgnore
    public static final Role ROLE_USER_VIEW_READ = new Role("ROLE_USER_VIEW_READ");
    @JsonIgnore
    public static final Role ROLE_USER_VIEW_UPDATE = new Role("ROLE_USER_VIEW_UPDATE");
    @JsonIgnore
    public static final Role ROLE_USER_VIEW_DELETE = new Role("ROLE_USER_VIEW_DELETE");
    @JsonIgnore
    public static final Role ROLE_USER_SIMPLE_USE = new Role("ROLE_USER_SIMPLE_USE");

    @JsonIgnore
    public static final Role ROLE_WORK_TEAM_CREATE = new Role("ROLE_WORK_TEAM_CREATE");
    @JsonIgnore
    public static final Role ROLE_WORK_TEAM_READ = new Role("ROLE_WORK_TEAM_READ");
    @JsonIgnore
    public static final Role ROLE_WORK_TEAM_UPDATE = new Role("ROLE_WORK_TEAM_UPDATE");
    @JsonIgnore
    public static final Role ROLE_WORK_TEAM_DELETE = new Role("ROLE_WORK_TEAM_DELETE");
    @JsonIgnore
    public static final Role ROLE_WORK_TEAM_SIMPLE_USE = new Role("ROLE_WORK_TEAM_SIMPLE_USE");

    @JsonIgnore
    public static final Role ROLE_MOBILE_WORK_TEAM_CREATE = new Role("ROLE_MOBILE_WORK_TEAM_CREATE");
    @JsonIgnore
    public static final Role ROLE_MOBILE_WORK_TEAM_READ = new Role("ROLE_MOBILE_WORK_TEAM_READ");
    @JsonIgnore
    public static final Role ROLE_MOBILE_WORK_TEAM_UPDATE = new Role("ROLE_MOBILE_WORK_TEAM_UPDATE");
    @JsonIgnore
    public static final Role ROLE_MOBILE_WORK_TEAM_DELETE = new Role("ROLE_MOBILE_WORK_TEAM_DELETE");

    @JsonCreator
    public Role(@JsonProperty("roleName") final String roleName) {
        if (!roleName.toUpperCase().startsWith("ROLE_")) {
            this.roleName = "ROLE_" + roleName.toUpperCase();
        } else {
            this.roleName = roleName.toUpperCase();
        }
        values.add(this);
    }

    public String getRoleName() {
        return roleName;
    }

    /**
     * Retorno o nome de maneira simples da role.
     * Por exemplo, considere a role 'ROLE_WOKR_TEAM_READ'
     * o simple name dela será 'WORK_TEAM'
     */
    @JsonIgnore
    public String getSimpleName() {
        return this.getRoleName().replace("ROLE_", "")
                .replace("_CREATE", "")
                .replace("_READ", "")
                .replace("_UPDATE", "")
                .replace("_DELETE", "")
                .replace("_SIMPLE_USE", "");
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
                .filter(it -> it.getRoleName().equalsIgnoreCase(value))
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
                        if (it.getRoleName().equalsIgnoreCase(v)) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toSet())
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
        return Stream.of(roles)
                .map(r -> Role.toPatternRole(endNameRole, r))
                .toArray(String[]::new);
    }
}
