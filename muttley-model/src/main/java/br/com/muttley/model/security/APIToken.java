package br.com.muttley.model.security;

import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.Model;
import br.com.muttley.model.security.jackson.UserDeserializer;
import br.com.muttley.model.security.jackson.UserSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 08/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "#{documentNameConfig.getNameCollectionAPIToken()}")
@CompoundIndexes({
        @CompoundIndex(name = "owner_index", def = "{'owner' : 1}"),
        @CompoundIndex(name = "owner.id_index", def = "{'owner.$id' : 1}"),
        @CompoundIndex(name = "owner.id_user.$id_index", def = "{'owner.$id' : 1, 'user.$id' : 1}"),
        @CompoundIndex(name = "token.index", def = "{'token' : 1}")
})
@Getter
@Setter
@Accessors(chain = true)
public class APIToken implements Model<Owner> {
    @Id
    private String id;
    @DBRef
    private Owner owner;
    @NotNull(message = "É nécessário ter um usuário master no grupo de trabalho")
    @JsonSerialize(using = UserSerializer.class)
    @JsonDeserialize(using = UserDeserializer.class)
    @DBRef
    private User user;
    @NotNull
    private Date dtCreate;
    @NotNull
    private String version;
    private String description;
    @NotEmpty(message = "É necessário ter um token devidamente gerado")
    private String token;
    private MetadataDocument metadata;

    @JsonIgnore
    public String generateSeedHash() {
        final Map<String, Object> map = new HashMap<>();
        map.put("owner", this.owner != null ? owner.getId() : null);
        map.put("user", this.user != null ? user.getId() : null);
        map.put("dtCreate", this.dtCreate);
        map.put("version", this.version);
        map.put("description", this.description);

        try {
            return new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(map);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
