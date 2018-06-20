package br.com.muttley.security.server.service;

import br.com.muttley.domain.service.Service;
import br.com.muttley.model.security.Owner;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface OwnerService extends Service<Owner> {
    Owner findByName(final String name);
}
