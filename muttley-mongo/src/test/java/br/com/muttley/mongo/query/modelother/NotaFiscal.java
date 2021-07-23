package br.com.muttley.mongo.query.modelother;

import br.com.muttley.model.security.Owner;
import br.com.muttley.annotations.CompoundIndexes;
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
import java.util.List;

import static br.com.muttley.mongo.query.modelother.NotaFiscal.COLLECTION_NOTAS_FISCAIS;

/**
 * @author Joel Rodrigues Moreira on 20/10/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = COLLECTION_NOTAS_FISCAIS)
@CompoundIndexes({
        @CompoundIndex(name = "owner_sync_index_unique", def = "{'owner' : 1, 'sync' : 1}", unique = true),
        @CompoundIndex(name = "owner_ciclo_index", def = "{'owner' : 1, 'ciclo' : 1}"),
        @CompoundIndex(name = "owner_ciclo_conta_index", def = "{'owner' : 1, 'ciclo' : 1, 'conta' : 1}"),
})
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class NotaFiscal {

    @Transient
    @JsonIgnore
    public static final String COLLECTION_NOTAS_FISCAIS = "notas_fiscais";

    @Id
    private String id;

    @JsonIgnore
    @DBRef
    private Owner owner;

    @DBRef
    private Empresa empresa;

    private List<NotaFiscalItem> itens;

    private String numero;
    private String serie;

    private Date dtEmissao;


    private String sync;


    private Date dtSync;

}
