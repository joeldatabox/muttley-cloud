package br.com.muttley.security.server.service;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.RecoveryPasswordResponse;
import br.com.muttley.model.security.RecoveryPayload;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserPayLoad;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
public interface UserService {
    User save(final UserPayLoad value);

    User save(final User user);

    /**
     * Salva as preferencias de um determinado usuário
     */
    void save(final User user, final UserPreferences preferences);

    boolean remove(final User user);

    boolean removeByUserName(final String userName);

    User update(final User user, final JwtToken token);

    User update(final User user, final User userForUpdate);

    /*User updatePasswd(final PasswdPayload user);*/

    User findByUserName(final String userName);

    User findUserByEmailOrUserNameOrNickUsers(final String email, final String userName, final Set<String> nickUsers);

    User findUserByEmailOrUserNameOrNickUser(final String emailOrUserName);

    boolean existUserByEmailOrUserNameOrNickUsers(final String email, final String userName, final Set<String> nickUsers);

    boolean userNameIsAvaliableForUserName(final String userName, final Set<String> userNames);

    boolean userNameIsAvaliable(final Set<String> userNames);

    boolean userNameIsAvaliable(final String userName);

    User findById(final String id);

    Collection<User> findAll();

    User getUserFromToken(final JwtToken token);

    UserPreferences loadPreference(final User user);

    /**
     * Retorna um usuários baseado em uma preferencia
     */
    List<User> getUsersFromPreference(final Preference preference);

    User getUserFromPreference(final Preference preference);

    boolean constainsPreference(final User user, final String keyPreference);

    RecoveryPasswordResponse recoveryPassword(RecoveryPayload recovery);
}
