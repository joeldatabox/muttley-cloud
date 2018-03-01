package br.com.muttley.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Interface utilizada pra implementação de multi tenancy no sistema
 *
 * @author Joel Rodrigues Moreira on 29/01/18.
 * @project muttley-cloud
 */
@Document(collection = "owners")
public interface Owner extends Serializable {
    public ObjectId getId();

    public Owner setId(final ObjectId id);

    public String getName();

    public Owner setName(final String name);

    public String getDescription();

    public Owner setDescription(final String description);
}
