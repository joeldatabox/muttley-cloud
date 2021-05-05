package br.com.muttley.model.admin;

import br.com.muttley.annotations.index.CompoundIndexes;
import br.com.muttley.model.security.UserDataBinding;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import static br.com.muttley.model.admin.AdminUserDataBinding.TYPE_ALIAS;

/**
 * @author Joel Rodrigues Moreira 27/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = "#{documentNameConfig.getNameCollectionAdminUserDataBinding()}")
@CompoundIndexes({
        @CompoundIndex(name = "owner_user_key_index_unique", def = "{'owner': 1, 'user': 1, 'key': 1}", unique = true)
})
@TypeAlias(TYPE_ALIAS)
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = {"key"})
public class AdminUserDataBinding extends UserDataBinding {
    @Transient
    @JsonIgnore
    public static final String TYPE_ALIAS = "admin-user-data-binding";

    public AdminUserDataBinding() {
    }

    public AdminUserDataBinding(final UserDataBinding dataBinding) {
        this.setId(dataBinding.getId())
                .setOwner(dataBinding.getOwner())
                .setUser(dataBinding.getUser())
                .setHistoric(dataBinding.getHistoric())
                .setKey(dataBinding.getKey())
                .setMetadata(dataBinding.getMetadata())
                .setResolvedValue(dataBinding.getResolvedValue())
                .setResolved(dataBinding.isResolved())
                .setValue(dataBinding.getValue());
    }
}
