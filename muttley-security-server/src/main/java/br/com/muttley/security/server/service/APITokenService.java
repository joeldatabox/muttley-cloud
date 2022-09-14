package br.com.muttley.security.server.service;

import br.com.muttley.model.security.APIToken;
import br.com.muttley.model.security.User;

/**
 * @author Joel Rodrigues Moreira on 08/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface APITokenService extends SecurityService<APIToken> {

    User loadUserByAPIToken(final String token);

}
