package br.com.muttley.model;

import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.exception.throwables.MuttleyInvalidObjectIdException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.PropertyUtils;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 22/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Document extends Serializable {

    String getId();

    Document setId(final String id);

    MetadataDocument getMetadata();

    Document setMetadata(final MetadataDocument metaData);

    Document setHistoric(final Historic historic);


    @JsonIgnore
    Historic getHistoric();

    @JsonIgnore
    default boolean containsMetadata() {
        return this.getMetadata() != null;
    }

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

    static Object getPropertyFrom(final Object instance, final String nameProperty) {
        try {
            if (instance == null) {
                return null;
            }
            return PropertyUtils.getProperty(instance, nameProperty);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new MuttleyException(e);
        }
    }
}
