package br.com.muttley.model.security;

import br.com.muttley.annotations.index.CompoundIndexes;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.Model;
import br.com.muttley.model.jackson.converter.DocumentSerializer;
import br.com.muttley.model.security.jackson.OwnerDeserializer;
import br.com.muttley.model.security.jackson.UserDataDeserializer;
import br.com.muttley.model.security.jackson.UserDataSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira 12/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Armazena dados que devem ser lincados ao usuário por owners
 */
@Document(collection = "#{documentNameConfig.getNameCollectionUserDataBinding()}")
@CompoundIndexes({
        @CompoundIndex(name = "owner_user_key_index_unique", def = "{'owner': 1, 'user': 1, 'key': 1}", unique = true)
})
@TypeAlias(UserDataBinding.TYPE_ALIAS)
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = {"key"})
public class UserDataBinding implements Model {
    @Transient
    @JsonIgnore
    public static final String TYPE_ALIAS = "user-data-binding";

    private String id;
    @JsonSerialize(using = DocumentSerializer.class)
    @JsonDeserialize(using = OwnerDeserializer.class)
    @DBRef
    private Owner owner;
    @NotNull(message = "Informe um usuário")
    @JsonSerialize(using = UserDataSerializer.class)
    @JsonDeserialize(using = UserDataDeserializer.class)
    @DBRef
    private UserData user;
    private String key;
    private String value;
    private MetadataDocument metadata;
    private Historic historic;
}
