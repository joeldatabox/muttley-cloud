package br.com.muttley.model.hermes.notification;


import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.security.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Document(collection = "#{documentNameConfig.getNameCollectionUserTokensNotification()}")
@CompoundIndexes({
        @CompoundIndex(name = "user_index_unique", def = "{'user' : 1}", unique = true)
})
@Getter
@Setter
@Accessors(chain = true)
@TypeAlias("tokens-notification")
public class UserTokensNotification implements br.com.muttley.model.Document {
    private String id;
    @NotNull(message = "Informe o usuário")
    /*@JsonSerialize(using = UserSerializer.class)
    @JsonDeserialize(using = UserDeserializer.class)*/
    @DBRef
    private User user;
    private Set<TokenId> tokens;
    private MetadataDocument metadata;

    public UserTokensNotification() {
        this.tokens = new HashSet<>();
    }

    public UserTokensNotification add(final TokenId tokenId) {
        this.tokens.add(tokenId);
        return this;
    }

    @JsonIgnore
    public Set<TokenId> getTokensMobile() {
        return this.tokens.parallelStream().filter(TokenId::isMobile).collect(Collectors.toSet());
    }
}
