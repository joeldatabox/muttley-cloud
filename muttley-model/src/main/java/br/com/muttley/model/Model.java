package br.com.muttley.model;

import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Joel Rodrigues Moreira on 29/01/18.
 * @project muttley-cloud
 */
public interface Model<T extends OwnerData> extends Document {

    default Model setOwner(final User user) {
        return this.setOwner((T) user.getCurrentOwner());
    }

    @JsonProperty("owner")
    Model setOwner(final T owner);

    @JsonIgnore
    OwnerData getOwner();

}
