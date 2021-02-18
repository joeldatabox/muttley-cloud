package br.com.muttley.mongo.query.model2;


import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;
import java.util.Objects;

;

/**
 * @author Joel Rodrigues Moreira on 21/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class PessoaJuridica {
    @Transient
    @JsonIgnore
    public static final String COLLECTION_EMPRESAS = "empresas";
    @Transient
    @JsonIgnore
    public static final String COLLECTION_FILIAIS = "filiais";

    @Id
    protected String id;

    @JsonIgnore
    @DBRef
    protected Owner owner;
    protected Historic historic;

    @Getter
    @Setter
    @Accessors(chain = true)
    protected MetadataDocument metadata;


    protected String razao;

    protected String fantasia;

    protected String cnpj;

    protected String inscEstadual;
    protected String telefone;

    protected String email;
    protected String site;

    protected String sync;

    protected Date dtSync;


    @JsonIgnore
    public String getCollection() {
        return null;
    }


    public String getId() {
        return id;
    }


    public PessoaJuridica setId(final String id) {
        this.id = id;
        return this;
    }


    public Owner getOwner() {
        return owner;
    }


    public PessoaJuridica setOwner(final Owner owner) {
        this.owner = owner;
        return this;
    }


    public PessoaJuridica setOwner(final User user) {
        this.owner = user.getCurrentOwner();
        return this;
    }


    public Historic getHistoric() {
        return historic;
    }


    public PessoaJuridica setHistoric(final Historic historic) {
        this.historic = historic;
        return this;
    }

    public String getRazao() {
        return razao;
    }

    public PessoaJuridica setRazao(final String razao) {
        this.razao = razao;
        return this;
    }

    public String getFantasia() {
        return fantasia;
    }

    public PessoaJuridica setFantasia(final String fantasia) {
        this.fantasia = fantasia;
        return this;
    }


    public String getCnpj() {
        return cnpj;
    }

    public PessoaJuridica setCnpj(final String cnpj) {
        this.cnpj = cnpj != null ? cnpj.replaceAll("[./-]", "") : cnpj;
        return this;
    }

    public String getInscEstadual() {
        return inscEstadual;
    }

    public PessoaJuridica setInscEstadual(final String inscEstadual) {
        this.inscEstadual = inscEstadual;
        return this;
    }

    public String getTelefone() {
        return telefone;
    }

    public PessoaJuridica setTelefone(final String telefone) {
        this.telefone = telefone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public PessoaJuridica setEmail(final String email) {
        this.email = email;
        return this;
    }


    public String getSite() {
        return site;
    }

    public PessoaJuridica setSite(final String site) {
        this.site = site;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, -25);
    }


    public String getSync() {
        return this.sync;
    }


    public PessoaJuridica setSync(final String sync) {
        this.sync = sync;
        return this;
    }

    public Date getDtSync() {
        return dtSync;
    }

    public PessoaJuridica setDtSync(final Date dtSync) {
        this.dtSync = dtSync;
        return this;
    }


}
