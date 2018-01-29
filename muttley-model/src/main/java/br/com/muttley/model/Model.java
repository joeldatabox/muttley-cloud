package br.com.muttley.model;

import br.com.muttley.model.security.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 29/01/18.
 * @project muttley-cloud
 */
public interface Model<T> extends Serializable {
    T getId();

    Model setId(T id);

    Model setDtCreate(Date dtCreate);

    Date getDtCreate();

    Model setLastUpdate(Date dtUpdate);

    Date getLastUpdate();

    Model setOwner(User user);

    User getOwner();

    Model setCreatedBy(User user);

    @JsonIgnore
    User getCreatedBy();

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
