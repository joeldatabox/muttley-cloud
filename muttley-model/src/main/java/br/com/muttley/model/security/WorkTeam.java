package br.com.muttley.model.security;

import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "#{@documentNameConfig.getNameCollectionWorkTeam()}")
@CompoundIndexes({
        @CompoundIndex(name = "name_userMaster_index_unique", def = "{'name' : 1, 'userMaster': 1}", unique = true)
})
@TypeAlias("muttley-work-teams")
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class WorkTeam implements Document {
    @Id
    protected String id;
    @NotBlank(message = "Informe um nome válido para o grupo")
    protected String name;
    protected String description;
    @NotNull(message = "É nécessário ter um usuário master no grupo de trabalho")
    @DBRef
    protected User userMaster;
    @NotNull(message = "É nécessário informar quem é o owner do grupo de trabalho")
    @DBRef
    protected Owner owner;
    @DBRef
    protected Set<User> members;
    protected Historic historic;
    protected Set<Role> roles;

    public WorkTeam() {
        this.members = new LinkedHashSet<>();
        this.roles = new LinkedHashSet<>();
    }

    public WorkTeam setUserMaster(final User userMaster) {
        this.userMaster = userMaster;
        this.addMember(this.userMaster);
        return this;
    }

    public WorkTeam addMember(final User user) {
        this.members.add(user);
        return this;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public WorkTeam setRoles(final Set<Authority> roles) {
        this.roles = roles.stream().map(Authority::getRole).collect(Collectors.toSet());
        return this;
    }

    public WorkTeam addRole(final Authority role) {
        this.roles.add(role.getRole());
        return this;
    }

    public WorkTeam addRole(final Role role) {
        this.roles.add(role);
        return this;
    }

}
