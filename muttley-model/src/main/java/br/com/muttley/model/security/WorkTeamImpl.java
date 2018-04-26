package br.com.muttley.model.security;

import br.com.muttley.model.Historic;
import com.google.common.base.Objects;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class WorkTeamImpl implements WorkTeam {
    @Id
    protected ObjectId id;
    @NotBlank(message = "Informe um nome válido para o grupo")
    @Indexed
    protected String name;
    protected String description;
    @NotNull(message = "É nécessário ter um usuário master no grupo de trabalho")
    @Indexed
    @DBRef
    protected User userMaster;
    @NotNull(message = "É nécessário informar quem é o owner do grupo de trabalho")
    @DBRef
    protected Owner owner;
    @DBRef
    protected Set<User> members;
    protected Historic historic;
    protected Set<Authority> authorities;

    public WorkTeamImpl() {
        this.members = new LinkedHashSet<>();
        this.authorities = new LinkedHashSet<>();
    }

    @Override
    public ObjectId getId() {
        return id;
    }

    @Override
    public WorkTeamImpl setId(final ObjectId id) {
        this.id = id;
        return this;
    }

    @Override
    public WorkTeamImpl setId(final String id) {
        return setId(new ObjectId(id));
    }

    @Override
    public String getName() {
        return name;
    }

    public WorkTeamImpl setName(final String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public WorkTeamImpl setDescription(final String description) {
        this.description = description;
        return this;
    }

    @Override
    public User getUserMaster() {
        return userMaster;
    }

    public WorkTeamImpl setUserMaster(final User userMaster) {
        this.userMaster = userMaster;
        return this;
    }

    @Override
    public Owner getOwner() {
        return owner;
    }

    public WorkTeamImpl setOwner(final Owner owner) {
        this.owner = owner;
        return this;
    }

    @Override
    public Set<User> getMembers() {
        return members;
    }

    public WorkTeamImpl setMembers(final Set<User> members) {
        this.members = members;
        return this;
    }

    public WorkTeamImpl addMember(final User user) {
        this.members.add(user);
        return this;
    }

    @Override
    public Historic getHistoric() {
        return historic;
    }

    @Override
    public WorkTeamImpl setHistoric(final Historic historic) {
        this.historic = historic;
        return this;
    }

    @Override
    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public WorkTeamImpl setAuthorities(final Set<Authority> authorities) {
        this.authorities = authorities;
        return this;
    }

    public WorkTeamImpl addAuthority(final Authority authority) {
        this.authorities.add(authority);
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkTeamImpl)) return false;
        WorkTeamImpl workTeam = (WorkTeamImpl) o;
        return Objects.equal(id, workTeam.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, -33);
    }
}
