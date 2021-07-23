package br.com.muttley.model.admin;

import br.com.muttley.annotations.index.CompoundIndexes;
import br.com.muttley.model.security.Owner;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import static br.com.muttley.model.admin.AdminOwner.TYPE_ALIAS;

/**
 * @author Joel Rodrigues Moreira 20/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = "#{documentNameConfig.getNameCollectionAdminOwner()}")
@CompoundIndexes({
        @CompoundIndex(name = "userMaster_index_unique", def = "{'userMaster': 1}", unique = true),
        @CompoundIndex(name = "name_index", def = "{'name': 1}")
})
@TypeAlias(TYPE_ALIAS)
public class AdminOwner extends Owner {
    @Transient
    @JsonIgnore
    public static final String TYPE_ALIAS = "admin-owner";
}
