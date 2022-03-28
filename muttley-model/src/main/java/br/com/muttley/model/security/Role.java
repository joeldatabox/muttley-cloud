package br.com.muttley.model.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;

import java.util.Arrays;
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
    public static final Role ROLE_ODIN_USER = new Role("ROLE_ODIN_USER");

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
    public static final Role ROLE_USER_BASE_CREATE = new Role("ROLE_USER_BASE_CREATE");
    @JsonIgnore
    public static final Role ROLE_USER_BASE_READ = new Role("ROLE_USER_BASE_READ");
    @JsonIgnore
    public static final Role ROLE_USER_BASE_UPDATE = new Role("ROLE_USER_BASE_UPDATE");
    @JsonIgnore
    public static final Role ROLE_USER_BASE_DELETE = new Role("ROLE_USER_BASE_DELETE");
    @JsonIgnore
    public static final Role ROLE_USER_BASE_SIMPLE_USE = new Role("ROLE_USER_BASE_SIMPLE_USE");

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
    public static final Role ROLE_PASSAPORT_CREATE = new Role("ROLE_PASSAPORT_CREATE");
    @JsonIgnore
    public static final Role ROLE_PASSAPORT_READ = new Role("ROLE_PASSAPORT_READ");
    @JsonIgnore
    public static final Role ROLE_PASSAPORT_UPDATE = new Role("ROLE_PASSAPORT_UPDATE");
    @JsonIgnore
    public static final Role ROLE_PASSAPORT_DELETE = new Role("ROLE_PASSAPORT_DELETE");
    @JsonIgnore
    public static final Role ROLE_PASSAPORT_SIMPLE_USE = new Role("ROLE_PASSAPORT_SIMPLE_USE");

    @JsonIgnore
    public static final Role ROLE_MOBILE_PASSAPORT_CREATE = new Role("ROLE_MOBILE_PASSAPORT_CREATE");
    @JsonIgnore
    public static final Role ROLE_MOBILE_PASSAPORT_READ = new Role("ROLE_MOBILE_PASSAPORT_READ");
    @JsonIgnore
    public static final Role ROLE_MOBILE_PASSAPORT_UPDATE = new Role("ROLE_MOBILE_PASSAPORT_UPDATE");
    @JsonIgnore
    public static final Role ROLE_MOBILE_PASSAPORT_DELETE = new Role("ROLE_MOBILE_PASSAPORT_DELETE");

    @JsonIgnore
    public static final Role ROLE_USER_DATA_BINDING_CREATE = new Role("ROLE_USER_DATA_BINDING_CREATE");
    @JsonIgnore
    public static final Role ROLE_USER_DATA_BINDING_READ = new Role("ROLE_USER_DATA_BINDING_READ");
    @JsonIgnore
    public static final Role ROLE_USER_DATA_BINDING_UPDATE = new Role("ROLE_USER_DATA_BINDING_UPDATE");
    @JsonIgnore
    public static final Role ROLE_USER_DATA_BINDING_DELETE = new Role("ROLE_USER_DATA_BINDING_DELETE");
    @JsonIgnore
    public static final Role ROLE_USER_DATA_BINDING_SIMPLE_USE = new Role("ROLE_USER_DATA_BINDING_SIMPLE_USE");
    @JsonIgnore
    public static final Role ROLE_USER_DATA_BINDING_OTHERS_USERS_MERGE = new Role("ROLE_USER_DATA_BINDING_SIMPLE_USE");

    @JsonIgnore
    public static final Role ROLE_MOBILE_USER_DATA_BINDING_CREATE = new Role("ROLE_MOBILE_USER_DATA_BINDING_CREATE");
    @JsonIgnore
    public static final Role ROLE_MOBILE_USER_DATA_BINDING_READ = new Role("ROLE_MOBILE_USER_DATA_BINDING_READ");
    @JsonIgnore
    public static final Role ROLE_MOBILE_USER_DATA_BINDING_UPDATE = new Role("ROLE_MOBILE_USER_DATA_BINDING_UPDATE");
    @JsonIgnore
    public static final Role ROLE_MOBILE_USER_DATA_BINDING_DELETE = new Role("ROLE_MOBILE_USER_DATA_BINDING_DELETE");
    @JsonIgnore
    public static final Role ROLE_MOBILE_USER_DATA_BINDING_OTHERS_USERS_MERGE = new Role("ROLE_MOBILE_USER_DATA_BINDING_SIMPLE_USE");

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

    @JsonIgnore
    @Transient
    public static Set<Role> getValues() {
        return Role.values;
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
                .parallelStream()
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
                .parallelStream()
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
                .parallel()
                .map(r -> Role.toPatternRole(endNameRole, r))
                .toArray(String[]::new);
    }

    public static void initializeRoles() {
        Role.values.addAll(
                Arrays.asList(
                        Role.ROLE_OWNER,
                        Role.ROLE_ROOT,
                        Role.ROLE_ODIN_USER,
                        Role.ROLE_ACCESS_PLAN_CREATE,
                        Role.ROLE_ACCESS_PLAN_READ,
                        Role.ROLE_ACCESS_PLAN_UPDATE,
                        Role.ROLE_ACCESS_PLAN_DELETE,
                        Role.ROLE_ACCESS_PLAN_SIMPLE_USE,
                        Role.ROLE_MOBILE_ACCESS_PLAN_CREATE,
                        Role.ROLE_MOBILE_ACCESS_PLAN_READ,
                        Role.ROLE_MOBILE_ACCESS_PLAN_UPDATE,
                        Role.ROLE_MOBILE_ACCESS_PLAN_DELETE,
                        Role.ROLE_OWNER_CREATE,
                        Role.ROLE_OWNER_READ,
                        Role.ROLE_OWNER_UPDATE,
                        Role.ROLE_OWNER_DELETE,
                        Role.ROLE_OWNER_SIMPLE_USE,
                        Role.ROLE_USER_BASE_CREATE,
                        Role.ROLE_USER_BASE_READ,
                        Role.ROLE_USER_BASE_UPDATE,
                        Role.ROLE_USER_BASE_DELETE,
                        Role.ROLE_USER_BASE_SIMPLE_USE,
                        Role.ROLE_USER_VIEW_CREATE,
                        Role.ROLE_USER_VIEW_READ,
                        Role.ROLE_USER_VIEW_UPDATE,
                        Role.ROLE_USER_VIEW_DELETE,
                        Role.ROLE_USER_SIMPLE_USE,
                        Role.ROLE_PASSAPORT_CREATE,
                        Role.ROLE_PASSAPORT_READ,
                        Role.ROLE_PASSAPORT_UPDATE,
                        Role.ROLE_PASSAPORT_DELETE,
                        Role.ROLE_PASSAPORT_SIMPLE_USE,
                        Role.ROLE_MOBILE_PASSAPORT_CREATE,
                        Role.ROLE_MOBILE_PASSAPORT_READ,
                        Role.ROLE_MOBILE_PASSAPORT_UPDATE,
                        Role.ROLE_MOBILE_PASSAPORT_DELETE,
                        Role.ROLE_USER_DATA_BINDING_CREATE,
                        Role.ROLE_USER_DATA_BINDING_READ,
                        Role.ROLE_USER_DATA_BINDING_UPDATE,
                        Role.ROLE_USER_DATA_BINDING_DELETE,
                        Role.ROLE_USER_DATA_BINDING_SIMPLE_USE,
                        Role.ROLE_USER_DATA_BINDING_OTHERS_USERS_MERGE,
                        Role.ROLE_MOBILE_USER_DATA_BINDING_CREATE,
                        Role.ROLE_MOBILE_USER_DATA_BINDING_READ,
                        Role.ROLE_MOBILE_USER_DATA_BINDING_UPDATE,
                        Role.ROLE_MOBILE_USER_DATA_BINDING_DELETE,
                        Role.ROLE_MOBILE_USER_DATA_BINDING_OTHERS_USERS_MERGE
                ));
    }
}
