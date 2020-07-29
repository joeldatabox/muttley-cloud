package br.com.muttley.mongo.migration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

/**
 * @author Joel Rodrigues Moreira on 28/07/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = MuttleyMigrationModel.COLLECTION)
@CompoundIndexes({
        @CompoundIndex(name = MuttleyMigrationModel.NAME_INDEX, def = MuttleyMigrationModel.DEF_INDEX, unique = true)
})
@TypeAlias("muttley-migration")
public class MuttleyMigrationModel {
    @Transient
    @JsonIgnore
    public static final String COLLECTION = "muttley-migrations";
    @Transient
    @JsonIgnore
    public static final String NAME_INDEX = "version_unique";
    @Transient
    @JsonIgnore
    public static final String DEF_INDEX = "{'version' : 1}";

    @Id
    private String id;
    private long version;
    private Date updatedIn;
    private String description;

    public MuttleyMigrationModel(final MuttleyMigrationSource source) {
        this.version = source.getVersion();
        this.description = source.getDescription();
        this.updatedIn = new Date();
    }

    public MuttleyMigrationModel() {
    }

    public String getId() {
        return id;
    }

    public MuttleyMigrationModel setId(final String id) {
        this.id = id;
        return this;
    }

    public long getVersion() {
        return version;
    }

    public MuttleyMigrationModel setVersion(final long version) {
        this.version = version;
        return this;
    }

    public Date getUpdatedIn() {
        return updatedIn;
    }

    public MuttleyMigrationModel setUpdatedIn(final Date updatedIn) {
        this.updatedIn = updatedIn;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MuttleyMigrationModel setDescription(final String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MuttleyMigrationModel)) return false;
        final MuttleyMigrationModel that = (MuttleyMigrationModel) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

