package br.com.muttley.model.security;

import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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
public class UserBase implements Model {
    @Id
    private String id;

    @JsonIgnore
    @DBRef
    private Owner owner;

    @Min(value = 1, message = "Informe pelo menos um usuário")
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

    public UserBase addUser(final User currentUser, final User user) {
        if (user != null) {
            this.addUser(new UserBaseItem(currentUser, user, new Date(), true));
        }
        return this;
    }
}
