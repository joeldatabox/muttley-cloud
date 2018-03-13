package br.com.muttley.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

/**
 * @author Joel Rodrigues Moreira on 22/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Document<T extends Serializable> extends Serializable {

    T getId();

    Document setId(final T id);

    Document setId(final String id);

    Document setHistoric(final Historic historic);

    @JsonIgnore
    Historic getHistoric();

    default String toJson() {
        try {
            return new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
