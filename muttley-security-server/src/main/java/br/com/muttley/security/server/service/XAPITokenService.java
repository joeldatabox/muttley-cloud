package br.com.muttley.security.server.service;

import br.com.muttley.model.security.XAPIToken;
import br.com.muttley.model.security.User;

/**
 * @author Joel Rodrigues Moreira on 08/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface XAPITokenService extends SecurityService<XAPIToken> {

    XAPIToken loadUserByAPIToken(final String token);

    XAPIToken generateXAPIToken(final User user);
}
