package br.com.muttley.model;

import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;

/**
 * @author Joel Rodrigues Moreira on 29/01/18.
 * @project muttley-cloud
 */
public interface MultiTenancyModel extends Document {

    default MultiTenancyModel setOwner(final User user) {
        return this.setOwner(user.getCurrentOwner());
    }

    MultiTenancyModel setOwner(final Owner owner);

    Owner getOwner();

}
