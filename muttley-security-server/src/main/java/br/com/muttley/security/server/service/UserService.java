package br.com.muttley.security.server.service;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.Passwd;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
public interface UserService extends UserDetailsService {
    User save(final User user);

    /**
     * Salva as preferencias de um determinado usuário
     */
    void save(final User user, final UserPreferences preferences);

    boolean remove(final User user);

    boolean removeByUserName(final String userName);

    User update(final User user, final JwtToken token);

    User updatePasswd(final Passwd user);

    User findByUserName(final String userName);

    User findUserByEmailOrUserNameOrNickUsers(final String email, final String userName, final Set<String> nickUsers);

    boolean existUserByEmailOrUserNameOrNickUsers(final String email, final String userName, final Set<String> nickUsers);

    boolean userNameIsAvaliable(final Set<String> userNames);

    User findById(final String id);

    Collection<User> findAll();

    User getUserFromToken(final JwtToken token);

    /*Authentication getCurrentAuthentication();

    JwtUser getCurrentJwtUser();

    JwtToken getCurrentToken();

    User getCurrentUser();*/

    UserPreferences loadPreference(final User user);

    /**
     * Retorna um usuários baseado em uma preferencia
     */
    List<User> getUsersFromPreference(final Preference preference);

    User getUserFromPreference(final Preference preference);

    boolean constainsPreference(final User user, final String keyPreference);
}
