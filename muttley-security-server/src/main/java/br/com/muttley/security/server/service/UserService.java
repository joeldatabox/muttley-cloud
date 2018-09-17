package br.com.muttley.security.server.service;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.Passwd;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.UserPreferences;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;

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

    boolean removeByEmail(final String email);

    User update(final User user);

    User updatePasswd(final Passwd user);

    User findByEmail(final String email);

    User findById(final String id);

    Collection<User> findAll();

    User getUserFromToken(final JwtToken token);

    /*Authentication getCurrentAuthentication();

    JwtUser getCurrentJwtUser();

    JwtToken getCurrentToken();

    User getCurrentUser();*/

    UserPreferences loadPreference(final User user);
}
