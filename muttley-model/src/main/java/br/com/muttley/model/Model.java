package br.com.muttley.model;

import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import org.bson.types.ObjectId;

/**
 * @author Joel Rodrigues Moreira on 29/01/18.
 * @project muttley-cloud
 */
public interface Model<T extends ObjectId> extends Document<T> {

    Model setOwner(final User user);

    Model setOwner(final Owner owner);

    Owner getOwner();

}
