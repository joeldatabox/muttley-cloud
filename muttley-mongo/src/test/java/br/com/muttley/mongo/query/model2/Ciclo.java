package br.com.muttley.mongo.query.model2;


import br.com.muttley.model.Historic;
import br.com.muttley.model.jackson.converter.DateDeserializer;
import br.com.muttley.model.jackson.converter.DateSerializer;
import br.com.muttley.model.security.Owner;
import br.com.muttley.mongo.service.annotations.CompoundIndexes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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

/**
 * @author Joel Rodrigues Moreira on 21/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = Ciclo.COLLECTION_CICLOS)
@CompoundIndexes({
        @CompoundIndex(name = "owner_sync_index_unique", def = "{'owner' : 1, 'sync' : 1}", unique = true)
})
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class Ciclo {

    @Transient
    @JsonIgnore
    public static final String COLLECTION_CICLOS = "ciclos";

    @Id
    private String id;

    @JsonIgnore
    @DBRef
    private Owner owner;

    private String descricao;

    @JsonSerialize(using = DateSerializer.class)
    @JsonDeserialize(using = DateDeserializer.class)
    private Date dtInicio;

    @JsonSerialize(using = DateSerializer.class)
    @JsonDeserialize(using = DateDeserializer.class)
    private Date dtFinal;

    @JsonIgnore
    private Historic historic;
}
