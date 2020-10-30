package br.com.muttley.mongo.query.modelother;

import br.com.muttley.model.security.Owner;
import br.com.muttley.mongo.service.annotations.CompoundIndexes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

import static br.com.muttley.mongo.query.modelother.Produto.COLLECTION_PRODUTOS;

/**
 * @author Joel Rodrigues Moreira on 20/10/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = COLLECTION_PRODUTOS)
@CompoundIndexes({
        @CompoundIndex(name = "owner_sync_index_unique", def = "{'owner' : 1, 'sync' : 1}", unique = true)
})
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class Produto {

    @Transient
    @JsonIgnore
    public static final String COLLECTION_PRODUTOS = "produtos";

    @Id
    private String id;

    @JsonIgnore
    @DBRef
    private Owner owner;

    @DBRef
    private Empresa empresa;


    private String descricao;
    private String und;
    private String sync;

    private Date dtSync;
}
