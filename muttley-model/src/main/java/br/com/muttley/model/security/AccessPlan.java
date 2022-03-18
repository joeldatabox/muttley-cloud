package br.com.muttley.model.security;

import br.com.muttley.model.Document;
import br.com.muttley.model.MetadataDocument;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import javax.validation.constraints.Min;
import java.util.Objects;

/**
 * @author Joel Rodrigues Moreira on 17/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "#{documentNameConfig.getNameCollectionAccessPlan()}")
@CompoundIndexes({
        @CompoundIndex(name = "name_index_unique", def = "{'name' : 1}", unique = true)
})
public class AccessPlan implements Document {

    @Id
    private String id;
    private MetadataDocument metadata;
    @NotBlank(message = "Informe um nome válido")
    private String name;
    @Min(value = 1, message = "É necessário ter ao menos 1 usuário!")
    private int totalUsers;
    private String description;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public AccessPlan setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public MetadataDocument getMetadata() {
        return metadata;
    }

    @Override
    public AccessPlan setMetadata(final MetadataDocument metadata) {
        this.metadata = metadata;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public AccessPlan setName(String name) {
        this.name = name;
        return this;
    }

    public int getTotalUsers() {
        return this.totalUsers;
    }

    public AccessPlan setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public AccessPlan setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccessPlan)) return false;
        AccessPlan that = (AccessPlan) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, 9, 63);
    }
}
