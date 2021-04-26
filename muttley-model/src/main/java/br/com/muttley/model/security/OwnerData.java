package br.com.muttley.model.security;

import br.com.muttley.exception.throwables.MuttleyInvalidObjectIdException;
import br.com.muttley.model.security.jackson.OwnerDataDeserializerDefault;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.bson.types.ObjectId;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira 29/12/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Interface criada para Evitar o vazamente do informações  a respeito do owner para terceiros
 */
@JsonDeserialize(using = OwnerDataDeserializerDefault.class)
public interface OwnerData {
    String getId();

    String getName();

    String getDescription();

    UserData getUserMaster();

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
}
