package br.com.muttley.model.hermes.notification;


import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.jackson.UserDeserializer;
import br.com.muttley.model.security.jackson.UserSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Document(collection = "#{documentNameConfig.getNameCollectionUserTokensNotification()}")
@CompoundIndexes({
        @CompoundIndex(name = "user_index_unique", def = "{'user' : 1}", unique = true)
})
@Getter
@Setter
@Accessors(chain = true)
public class UserTokensNotification implements br.com.muttley.model.Document {
    private String id;
    @NotNull(message = "Informe o usuário")
    @JsonSerialize(using = UserSerializer.class)
    @JsonDeserialize(using = UserDeserializer.class)
    @DBRef
    private User user;
    private Set<TokenId> tokens;
    private MetadataDocument metadata;
    private Historic historic;
}
