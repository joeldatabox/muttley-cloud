package br.com.muttley.model.security;

import br.com.muttley.annotations.index.CompoundIndexes;
import br.com.muttley.exception.throwables.MuttleyInvalidObjectIdException;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.jackson.converter.DocumentSerializer;
import br.com.muttley.model.security.jackson.AccessPlanDeserializer;
import br.com.muttley.model.security.jackson.UserDeserializer;
import br.com.muttley.model.security.jackson.UserSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

import static br.com.muttley.model.security.Owner.TYPE_ALIAS;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = "#{documentNameConfig.getNameCollectionOwner()}")
@CompoundIndexes({
        @CompoundIndex(name = "userMaster_index_unique", def = "{'userMaster': 1}", unique = true),
        @CompoundIndex(name = "name_index", def = "{'name': 1}")
})
@TypeAlias(TYPE_ALIAS)
public class Owner implements br.com.muttley.model.Document, OwnerData {
    @Transient
    @JsonIgnore
    public static final String TYPE_ALIAS = "owner";

    @Id
    protected String id;
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
    protected MetadataDocument metadata;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Owner setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    public Owner setName(final String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public Owner setDescription(final String description) {
        this.description = description;
        return this;
    }

    @Override
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
    public MetadataDocument getMetadata() {
        return metadata;
    }

    @Override
    public Owner setMetadata(final MetadataDocument metaData) {
        this.metadata = metaData;
        return this;
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

    public OwnerData toOwnerData() {
        return new OwnerDataImpl(this);
    }

    @Override
    public ObjectId getObjectId() {
        if (!isEmpty(getId())) {
            try {
                return new ObjectId(getId());
            } catch (IllegalArgumentException ex) {
                throw new MuttleyInvalidObjectIdException(this.getClass(), "id", "ObjectId inválido");
            }
        }
        return null;
    }
}
