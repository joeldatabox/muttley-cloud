package br.com.muttley.model;

import br.com.muttley.exception.throwables.MuttleyInvalidObjectIdException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;

import java.io.Serializable;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 22/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Document extends Serializable {

    String getId();

    Document setId(final String id);

    MetaDataDocument getMetaData();

    Document setMetaData(final MetaDataDocument metaData);

    Document setHistoric(final Historic historic);


    @JsonIgnore
    Historic getHistoric();

    @JsonIgnore
    default ObjectId getObjectId() {
        if (!isEmpty(getId())) {
            try {
                return new ObjectId(getId());
            } catch (IllegalArgumentException ex) {
                throw new MuttleyInvalidObjectIdException(this.getClass(), "id", "ObjectId inválido");
            }
        }
        return null;
    }

    @JsonIgnore
    default boolean contaisObjectId() {
        if (!isEmpty(getId())) {
            return ObjectId.isValid(getId());
        }
        return false;
    }


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
