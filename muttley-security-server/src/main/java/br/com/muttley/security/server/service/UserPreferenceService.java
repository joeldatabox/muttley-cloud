package br.com.muttley.security.server.service;

import br.com.muttley.domain.Service;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;

/**
 * @author Joel Rodrigues Moreira on 01/11/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
public interface UserPreferenceService extends Service<UserPreferences> {

    UserPreferences save(final User user, UserPreferences userPreferences);

    UserPreferences getPreferences(final User user);

    UserPreferences getPreferences(final JwtToken token);

    void setPreferences(final User user, final Preference preferences);

    void setPreferences(final JwtToken token, final Preference preference);

    void removePreference(final User user, final String key);

    void removePreference(final JwtToken token, final String key);
}
