package br.com.muttley.model.security;

import br.com.muttley.model.Historic;
import com.google.common.base.Objects;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OwnerImpl implements Owner {
    @Id
    protected ObjectId id;
    protected String name;
    protected String description;
    protected Historic historic;

    @Override
    public ObjectId getId() {
        return id;
    }

    public OwnerImpl setId(final ObjectId id) {
        this.id = id;
        return this;
    }

    @Override
    public OwnerImpl setId(final String id) {
        setId(new ObjectId(id));
        return this;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public OwnerImpl setName(final String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public OwnerImpl setDescription(final String description) {
        this.description = description;
        return this;
    }

    @Override
    public OwnerImpl setHistoric(final Historic historic) {
        this.historic = historic;
        return this;
    }

    @Override
    public Historic getHistoric() {
        return this.historic;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if ((!(o instanceof OwnerImpl)) || (!(o instanceof Owner))) return false;
        OwnerImpl owner = (OwnerImpl) o;
        return Objects.equal(id, owner.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, 93);
    }
}
