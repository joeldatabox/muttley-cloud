package br.com.muttley.model.workteam;

import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.Model;
import br.com.muttley.model.jackson.converter.DocumentSerializer;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.jackson.OwnerDeserializer;
import br.com.muttley.model.security.jackson.UserCollectionSerializer;
import br.com.muttley.model.security.jackson.UserSetDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

import static br.com.muttley.model.workteam.WorkTeam.TYPE_ALIAS;


/**
 * @author Joel Rodrigues Moreira on 02/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "#{documentNameConfig.getNameCollectionWorkTeam()}")
@CompoundIndexes({
        @CompoundIndex(name = "owner_index", def = "{'owner' : 1}"),
        @CompoundIndex(name = "owner.id_index", def = "{'owner.$id' : 1}"),
        @CompoundIndex(name = "usersMaster.id_index", def = "{'usersMaster.$id' : 1}"),
        @CompoundIndex(name = "owner_usersMaster_index", def = "{'owner' : 1, 'usersMaster' : 1}"),
        @CompoundIndex(name = "owner.id_usersMaster.id_index", def = "{'owner.$id' : 1, 'usersMaster.$id' : 1}"),
        @CompoundIndex(name = "name_usersMaster_index_unique", def = "{'name' : 1, 'usersMaster': 1}", unique = true)
})
@TypeAlias(TYPE_ALIAS)
@Getter
@Setter
@Accessors(chain = true)
public class WorkTeam implements Model<Owner> {
    @Transient
    @JsonIgnore
    public static final String TYPE_ALIAS = "work-team";

    @Id
    protected String id;
    @NotBlank(message = "Informe um nome válido para o grupo")
    protected String name;
    protected String description;
    @NotNull(message = "É nécessário ter pelo usuário master no grupo de trabalho")
    @Size(min = 1, message = "É nécessário ter pelo usuário master no grupo de trabalho")
    @JsonSerialize(using = UserCollectionSerializer.class)
    @JsonDeserialize(using = UserSetDeserializer.class)
    @DBRef
    protected Set<User> usersMaster;
    /*@NotNull(message = "É nécessário informar quem é o owner do grupo de trabalho")
    @DBRef*/
    @JsonSerialize(using = DocumentSerializer.class)
    @JsonDeserialize(using = OwnerDeserializer.class)
    @DBRef
    protected Owner owner;
    @DBRef
    @JsonSerialize(using = UserCollectionSerializer.class)
    @JsonDeserialize(using = UserSetDeserializer.class)
    protected Set<User> members;
    protected MetadataDocument metadata;
    protected boolean editDataFromMembers = false;

    public boolean containsMember(final User userMaster) {
        if (this.membersIsEmpty()) {
            return false;
        }
        return this.getMembers()
                .parallelStream()
                .filter(user -> user.equals(userMaster))
                .count() > 0;
    }

    public boolean membersIsEmpty() {
        return CollectionUtils.isEmpty(this.members);
    }

    public boolean usersMasterIsEmpty() {
        return CollectionUtils.isEmpty(this.usersMaster);
    }

    /**
     * Verifica se tem algum membro presente no workteam
     * incluindo o usermaster
     */
    public boolean containsAnyUser() {
        return (!this.usersMasterIsEmpty()) || (!this.membersIsEmpty());
    }

    @JsonIgnore
    public Set<User> getAllUsers() {
        final Set<User> users = this.membersIsEmpty() ? new HashSet<>() : new HashSet<>(this.getMembers());

        if (!this.usersMasterIsEmpty()) {
            users.addAll(this.getUsersMaster());
        }
        return users;
    }
}
