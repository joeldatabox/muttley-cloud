package br.com.muttley.model;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.jackson.UserSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 31/01/18.
 * @project muttley-cloud
 */
public class Historic {
    @JsonSerialize(using = UserSerializer.class)
    @DBRef
    private User createdBy;
    private Date dtCreate;
    @DBRef
    private User lastChangeBy;
    private Date dtChange;

    public Historic() {
    }

    @JsonCreator
    public Historic(
            @JsonProperty("createdBy") final User createdBy,
            @JsonProperty("dtCreate") final Date dtCreate,
            @JsonProperty("lastChangeBy") final User lastChangeBy,
            @JsonProperty("dtChange") final Date dtChange) {
        this();
        this.createdBy = createdBy;
        this.dtCreate = dtCreate;
        this.lastChangeBy = lastChangeBy;
        this.dtChange = dtChange;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Historic setCreatedBy(final User createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public Date getDtCreate() {
        return dtCreate;
    }

    public Historic setDtCreate(final Date dtCreate) {
        this.dtCreate = dtCreate;
        return this;
    }

    public User getLastChangeBy() {
        return lastChangeBy;
    }

    public Historic setLastChangeBy(final User lastChangeBy) {
        this.lastChangeBy = lastChangeBy;
        return this;
    }

    public Date getDtChange() {
        return dtChange;
    }

    public Historic setDtChange(final Date dtChange) {
        this.dtChange = dtChange;
        return this;
    }
}
