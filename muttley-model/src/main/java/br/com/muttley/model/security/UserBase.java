package br.com.muttley.model.security;

import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira 25/11/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = "#{documentNameConfig.getNameCollectionUserBase()}")
@CompoundIndexes({
        @CompoundIndex(name = "owner_index_unique", def = "{'owner' : 1}", unique = true)
})
@Getter
@Setter
@Accessors(chain = true)
@TypeAlias(UserBase.TYPE_ALIAS)
public class UserBase implements Model {
    @Transient
    @JsonIgnore
    public static final String TYPE_ALIAS = "user-base";

    @Id
    private String id;

    @JsonIgnore
    @DBRef
    private Owner owner;

    @Size.List({@Size(min = 1, message = "Informe pelo menos um usuário")})
    @Valid
    private Set<UserBaseItem> users;

    private Historic historic;
    private MetadataDocument metadata;

    public UserBase() {
        this.users = new HashSet<>();
    }

    public UserBase addUser(final UserBaseItem userBaseItem) {
        if (userBaseItem != null) {
            this.users.add(userBaseItem);
        }
        return this;
    }

    public UserBase addUser(final UserView currentUser, final UserView user) {
        if (user != null) {
            this.addUser(new UserBaseItem(currentUser, user, new Date(), true));
        }
        return this;
    }
}
