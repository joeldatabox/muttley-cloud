package br.com.muttley.security.server.service;

import br.com.muttley.domain.service.Service;
import br.com.muttley.model.Owner;
import org.bson.types.ObjectId;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project agrifocus-cloud
 */
public interface OwnerService extends Service<Owner, ObjectId> {
    Owner findByName(final String name);
}
