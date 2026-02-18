package br.com.muttley.model.parametrizacao;

import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.NameAlias;
import br.com.muttley.model.security.Owner;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import static br.com.muttley.model.parametrizacao.Parametro.COLLECTION_PARAMETROS;

@Getter
@Setter
@Data
@Accessors(chain = true)
@Document(collection = COLLECTION_PARAMETROS)
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
