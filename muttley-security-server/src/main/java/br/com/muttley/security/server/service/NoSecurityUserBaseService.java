package br.com.muttley.security.server.service;

import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBase;
import br.com.muttley.model.security.UserBaseItem;
import br.com.muttley.model.security.UserData;
import br.com.muttley.model.security.UserView;
import br.com.muttley.model.security.merge.MergedUserBaseResponse;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 26/11/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface NoSecurityUserBaseService extends SecurityService<UserBase> {
    UserBase save(final User user, final OwnerData owner, final UserBase userBase);
}
