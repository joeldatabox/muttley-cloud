package br.com.muttley.mongo.query.model2;

import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.jackson.converter.DateDeserializer;
import br.com.muttley.model.jackson.converter.DateSerializer;
import br.com.muttley.model.security.Owner;
import br.com.muttley.mongo.service.annotations.CompoundIndexes;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

import java.math.BigDecimal;
import java.util.Date;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 21/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = Conta.COLLECTION_CONTAS)
@CompoundIndexes({
        @CompoundIndex(name = "owner_cpfCnpj_index_unique", def = "{'owner' : 1, 'cpfCnpj' : 1}"),
        @CompoundIndex(name = "owner_sync_index_unique", def = "{'owner' : 1, 'sync' : 1}", unique = true)
})
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class Conta {

    @Transient
    @JsonIgnore
    public static final String COLLECTION_CONTAS = "contas";

    @Id
    private String id;
    @JsonIgnore
    @DBRef

    private Owner owner;

    private String razao;

    private String fantasia;

    private String cpfCnpj;
    private String email;
    private String telefone1;
    private String telefone2;
    private String contato;

    private BigDecimal limite;

    private String inscEstadual;
    @JsonSerialize(using = DateSerializer.class)
    @JsonDeserialize(using = DateDeserializer.class)
    private Date dtNascimento;
    private Date dtCadastro;


    private String sync;

    private Date dtSync;
    @JsonIgnore
    private Historic historic;
    private MetadataDocument metadata;

    public Conta() {
    }

    @JsonCreator
    public Conta(
            @JsonProperty("id") final String id,

            @JsonProperty("razao") final String razao,
            @JsonProperty("fantasia") final String fantasia,
            @JsonProperty("cpfCnpj") final String cpfCnpj,
            @JsonProperty("email") final String email,
            @JsonProperty("telefone1") final String telefone1,
            @JsonProperty("telefone2") final String telefone2,

            @JsonProperty("inscEstadual") final String inscEstadual,

            @JsonProperty("dtNascimento") final Date dtNascimento,
            @JsonProperty("dtCadastro") final Date dtCadastro,
            @JsonProperty("sync") final String sync,
            @JsonProperty("dtSync") final Date dtSync) {
        this();
        this.id = id;
        this.razao = razao;
        this.fantasia = fantasia;
        //this.cpfCnpj = cpfCnpj;
        this.setCpfCnpj(cpfCnpj);
        this.email = email;
        this.telefone1 = telefone1;
        this.telefone2 = telefone2;

        this.inscEstadual = inscEstadual;

        this.dtNascimento = dtNascimento;
        this.dtCadastro = dtCadastro;
        this.sync = sync;
        this.dtSync = dtSync;
    }

    public Conta setCpfCnpj(final String cpfCnpj) {
        this.cpfCnpj = isEmpty(cpfCnpj) ? cpfCnpj : cpfCnpj.replaceAll("[./-]", "");
        return this;
    }

    public Conta setEmail(final String email) {
        this.email = email;
        if (this.email != null)
            this.email = this.email.toLowerCase();
        return this;
    }

}
