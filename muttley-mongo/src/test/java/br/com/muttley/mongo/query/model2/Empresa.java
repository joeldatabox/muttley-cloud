package br.com.muttley.mongo.query.model2;

import br.com.muttley.mongo.service.annotations.CompoundIndexes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

/**
 * @author Joel Rodrigues Moreira on 31/01/18.
 * @project muttley-cloud
 */
@Document
@CompoundIndexes({
        @CompoundIndex(name = "owner_cnpj_index_unique", def = "{'owner' : 1, 'cnpj' : 1}", unique = true),
        //@CompoundIndex(name = "owner_razao_index_unique", def = "{'owner' : 1, 'razao' : 1}", unique = true),
        @CompoundIndex(name = "owner_sync_index_unique", def = "{'owner' : 1, 'sync' : 1}", unique = true)
})
public class Empresa extends PessoaJuridica {
    private boolean principal;

    private int sequencial = 0;

    public Empresa() {
        this.principal = false;
    }

    @JsonIgnore
    @Transient
    @Override
    public String getCollection() {
        return PessoaJuridica.COLLECTION_EMPRESAS;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public Empresa setPrincipal(final boolean principal) {
        this.principal = principal;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Empresa)) return false;
        final Empresa empresa = (Empresa) o;
        return Objects.equals(id, empresa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, "empresa");
    }
}
