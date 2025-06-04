package br.com.muttley.model.parametrizacao;

import br.com.muttley.annotations.index.CompoundIndexes;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.NameAlias;
import br.com.muttley.model.security.Owner;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import static br.com.muttley.model.parametrizacao.Parametro.COLLECTION_PARAMETROS;

@Getter
@Setter
@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
@Document(collection = COLLECTION_PARAMETROS)
@CompoundIndexes({
        @CompoundIndex(name = "owner_index", def = "{'owner' : 1}"),
        @CompoundIndex(name = "owner.id_index", def = "{'owner.$id' : 1}"),
        @CompoundIndex(name = "usersMaster.id_index", def = "{'usersMaster.$id' : 1}"),
        @CompoundIndex(name = "owner_usersMaster_index", def = "{'owner' : 1, 'usersMaster' : 1}"),
        @CompoundIndex(name = "owner.id_usersMaster.id_index", def = "{'owner.$id' : 1, 'usersMaster.$id' : 1}")
})
@NameAlias(singularName = "Parametro", pluralName = "Parametros")
public class Parametro implements br.com.muttley.model.Document {

    @JsonIgnore
    @Transient
    public static final String COLLECTION_PARAMETROS = "parametros";

    @Id
    private String id;
    @JsonIgnore
    @DBRef
    private Owner owner;

    private String chave;
    private boolean valor;
    private String descricao;

    @Getter
    @Setter
    @Accessors(chain = true)
    private MetadataDocument metadata;

}
