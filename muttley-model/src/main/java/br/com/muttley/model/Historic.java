package br.com.muttley.model;

import br.com.muttley.model.security.model.User;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 31/01/18.
 * @project muttley-cloud
 */
public class Historic {
    @DBRef
    private User createdBy;
    @Indexed
    private Date dtCreate;
    @DBRef
    private User lastChangeBy;
    @Indexed
    private Date dtChange;

    public Historic() {
    }

    public Historic(final User createdBy, final Date dtCreate, final User lastChangeBy, final Date dtChange) {
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
