package br.com.muttley.model;

import br.com.muttley.model.security.model.User;

import java.io.Serializable;

/**
 * @author Joel Rodrigues Moreira on 29/01/18.
 * @project muttley-cloud
 */
public interface Model<T extends Serializable> extends Document<T> {

    Model setOwner(final User user);

    Model setOwner(final Owner owner);

    Owner getOwner();

}
