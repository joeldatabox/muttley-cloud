package br.com.muttley.security.server.service;

import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBase;

/**
 * @author Joel Rodrigues Moreira on 26/11/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface UserBaseService extends SecurityService<UserBase> {
    UserBase save(final User user, final Owner owner, final UserBase userBase);
}
