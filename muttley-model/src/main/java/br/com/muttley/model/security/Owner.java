package br.com.muttley.model.security;

import br.com.muttley.model.Historic;
import br.com.muttley.model.jackson.converter.DocumentSerializer;
import br.com.muttley.model.security.jackson.AccessPlanDeserializer;
import br.com.muttley.model.security.jackson.UserDeserializer;
import br.com.muttley.model.security.jackson.UserSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = "#{documentNameConfig.getNameCollectionOwner()}")
@CompoundIndexes({
        @CompoundIndex(name = "name_userMaster_index_unique", def = "{'name' : 1, 'userMaster': 1}", unique = true)
})
public class Owner implements br.com.muttley.model.Document {
    @Id
    protected String id;
    @Indexed
    protected String name;
    protected String description;
    @NotNull(message = "Informe o usuário master")
    @JsonSerialize(using = UserSerializer.class)
    @JsonDeserialize(using = UserDeserializer.class)
    @DBRef
    protected User userMaster;
    @JsonSerialize(using = DocumentSerializer.class)
    @JsonDeserialize(using = AccessPlanDeserializer.class)
    @DBRef
    protected AccessPlan accessPlan;
    protected Historic historic;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Owner setId(final String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Owner setName(final String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Owner setDescription(final String description) {
        this.description = description;
        return this;
    }

    public User getUserMaster() {
        return userMaster;
    }

    public Owner setUserMaster(final User userMaster) {
        this.userMaster = userMaster;
        return this;
    }

    public AccessPlan getAccessPlan() {
        return this.accessPlan;
    }

    public Owner setAccessPlan(final AccessPlan accessPlan) {
        this.accessPlan = accessPlan;
        return this;
    }

    public Owner setHistoric(final Historic historic) {
        this.historic = historic;
        return this;
    }

    public Historic getHistoric() {
        return this.historic;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if ((!(o instanceof Owner)) || (!(o instanceof Owner))) return false;
        Owner owner = (Owner) o;
        return Objects.equal(id, owner.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, 93);
    }
}
