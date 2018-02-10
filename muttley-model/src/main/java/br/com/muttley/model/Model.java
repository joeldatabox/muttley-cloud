package br.com.muttley.model;

import br.com.muttley.model.security.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

/**
 * @author Joel Rodrigues Moreira on 29/01/18.
 * @project muttley-cloud
 */
public interface Model<T> extends Serializable {
    T getId();

    Model setId(final T id);

    Model setOwner(final User user);

    Model setOwner(final Owner owner);

    Owner getOwner();

    Model setHistoric(final Historic historic);

    Historic getHistoric();
    /*ResourceSupport toResource();*/

    default String toJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
