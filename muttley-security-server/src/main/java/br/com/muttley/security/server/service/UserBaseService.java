package br.com.muttley.security.server.service;

import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBase;
import br.com.muttley.model.security.UserBaseItem;
import br.com.muttley.model.security.UserView;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 26/11/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface UserBaseService extends SecurityService<UserBase> {
    UserBase save(final User user, final Owner owner, final UserBase userBase);

    boolean userNameIsAvaliableForUserName(final User user, final String userName, final Set<String> userNames);

    boolean userNameIsAvaliable(final User user, final Set<String> userNames);

    UserView findUserByEmailOrUserNameOrNickUser(final User user, final String emailOrUserName);

    void addUserItemIfNotExists(final User user, final User userForAdd);

    void addUserItemIfNotExists(final User user, final UserBaseItem userForAdd);

    void createNewUserAndAdd(final User user, final UserBaseItem item);

    void mergeUserItemIfExists(User user, final UserBaseItem item);

    void removeByUserName(User user, String userName);
}
