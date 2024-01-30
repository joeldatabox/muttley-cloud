package br.com.muttley.model.security;

import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.Model;
import br.com.muttley.model.jackson.converter.DocumentSerializer;
import br.com.muttley.model.security.jackson.OwnerDeserializer;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 08/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "#{documentNameConfig.getNameCollectionXAPIToken()}")
@CompoundIndexes({
        @CompoundIndex(name = "owner_index", def = "{'owner' : 1}"),
        @CompoundIndex(name = "owner.id_index", def = "{'owner.$id' : 1}"),
        @CompoundIndex(name = "owner.id_user.$id_index", def = "{'owner.$id' : 1, 'user.$id' : 1}"),
        @CompoundIndex(name = "token.index", def = "{'token' : 1}")
})
@Getter
@Setter
@Accessors(chain = true)
public class XAPIToken implements Model<Owner>, UserDetails {
    @Id
    private String id;
    @DBRef
    @JsonSerialize(using = DocumentSerializer.class)
    @JsonDeserialize(using = OwnerDeserializer.class)
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
    @NotNull(message = "Informe uma descrição válida")
    private String description;
    @NotEmpty(message = "É necessário ter um token devidamente gerado")
    private String token;
    private String locaSeed;
    private MetadataDocument metadata;

    @JsonIgnore
    public String generateSeedHash() {
        final Map<String, Object> map = new HashMap<>(6);
        map.put("owner", this.owner != null ? owner.getId() : null);
        map.put("user", this.user != null ? user.getId() : null);
        map.put("dtCreate", this.dtCreate);
        map.put("version", this.version);
        map.put("description", this.description);
        map.put("localSeed", this.locaSeed);

        try {
            return new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(map);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public Date generateDtExpiration() {
        return Date.from(Instant.now().plus(Duration.ofHours(2)));
    }

    public boolean isEmpty() {
        return ObjectUtils.isEmpty(this.getToken());
    }

}
