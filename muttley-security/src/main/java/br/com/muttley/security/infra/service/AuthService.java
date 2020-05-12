package br.com.muttley.security.infra.service;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author Joel Rodrigues Moreira on 19/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface AuthService extends UserDetailsService {
    Authentication getCurrentAuthentication();

    JwtUser getCurrentJwtUser();

    JwtToken getCurrentToken();

    User getCurrentUser();

    UserPreferences getUserPreferences();

    Preference getPreference(final String key);
}
