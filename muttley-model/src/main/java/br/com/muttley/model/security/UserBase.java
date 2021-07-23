package br.com.muttley.model.security;

import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.MultiTenancyModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 22/07/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = "#{documentNameConfig.getNameCollectionUserBase()}")
@CompoundIndexes({
        @CompoundIndex(name = "owner_index_unique", def = "{'owner' : 1}", unique = true)
})
@Getter
@Setter
@Accessors(chain = true)
public class UserBase implements MultiTenancyModel {
    @Id
    private String id;

    @JsonIgnore
    @DBRef
    private Owner owner;

    private Set<User> users;

    private Historic historic;
    private MetadataDocument metadata;

    public UserBase() {
        this.users = new HashSet<>();
    }
}
