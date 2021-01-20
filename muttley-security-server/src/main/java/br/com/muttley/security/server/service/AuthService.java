package br.com.muttley.security.server.service;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserDataBinding;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import org.springframework.security.core.Authentication;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 04/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface AuthService {
    Authentication getCurrentAuthentication();

    JwtUser getCurrentJwtUser();

    JwtToken getCurrentToken();

    User getCurrentUser();

    UserPreferences getUserPreferences();

    Preference getPreference(final String key);

    Set<UserDataBinding> getDataBindings();

    UserDataBinding getDataBinding(final String key);
}
