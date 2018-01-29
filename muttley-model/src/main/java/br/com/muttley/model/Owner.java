package br.com.muttley.model;

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
    public String getId();

    public Owner setId(final String id);

    public String getDescription();

    public Owner setDescription(final String description);
}
