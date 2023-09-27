package br.com.muttley.model.admin;

import br.com.muttley.model.security.UserBase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import static br.com.muttley.model.admin.AdminUserBase.TYPE_ALIAS;

/**
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = "#{documentNameConfig.getNameCollectionAdminUserBase()}")
@CompoundIndexes({
        @CompoundIndex(name = "owner_index_unique", def = "{'owner' : 1}", unique = true)
})
@Getter
@Setter
@Accessors(chain = true)
@TypeAlias(TYPE_ALIAS)
public class AdminUserBase extends UserBase {
    @Transient
    @JsonIgnore
    public static final String TYPE_ALIAS = "admin-user-base";
}
