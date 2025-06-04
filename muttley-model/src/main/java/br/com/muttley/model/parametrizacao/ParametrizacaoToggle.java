package br.com.muttley.model.parametrizacao;

import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.NameAlias;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.jackson.ParametroCollectionSerializer;
import br.com.muttley.model.security.jackson.ParametroSetDeserializer;
import br.com.muttley.model.security.jackson.UserDeserializer;
import br.com.muttley.model.security.jackson.UserSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

import static br.com.muttley.model.parametrizacao.ParametrizacaoToggle.COLLECTION_PARAMETRIZACAO;

/**
 * @author Carolina Cedro on 14/05/2025.
 * e-mail: <a href="mailto:ana.carolina@maxxsoft.com">ana.carolina@maxxsoft.com</a>
 * @project agrifocus-cloud
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = COLLECTION_PARAMETRIZACAO)
@CompoundIndexes({
        @CompoundIndex(name = "owner_index", def = "{'owner' : 1}"),
        @CompoundIndex(name = "owner.id_index", def = "{'owner.$id' : 1}"),
        @CompoundIndex(name = "usersMaster.id_index", def = "{'usersMaster.$id' : 1}"),
        @CompoundIndex(name = "owner_usersMaster_index", def = "{'owner' : 1, 'usersMaster' : 1}"),
        @CompoundIndex(name = "owner.id_usersMaster.id_index", def = "{'owner.$id' : 1, 'usersMaster.$id' : 1}")
})
@Getter
@Setter
@TypeAlias(COLLECTION_PARAMETRIZACAO)
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
@NameAlias(singularName = "Parametrizacao-Toggle", pluralName = "Parametrizacaos-toggle")
public class ParametrizacaoToggle implements br.com.muttley.model.Document {

    @Transient
    @JsonIgnore
    public static final String COLLECTION_PARAMETRIZACAO = "parametrizacao-toggle";

    @Id
    private String id;

    @DBRef
    @JsonSerialize(using = UserSerializer.class)
    @JsonDeserialize(using = UserDeserializer.class)
    private User user;

    @NotNull(message = "Informe um parametro válido")
    @DBRef
    @JsonSerialize(using = ParametroCollectionSerializer.class)
    @JsonDeserialize(using = ParametroSetDeserializer.class)
    private Set<Parametro> parametros = new HashSet<>();


    private MetadataDocument metadata;

    public ParametrizacaoToggle() {
    }
}

