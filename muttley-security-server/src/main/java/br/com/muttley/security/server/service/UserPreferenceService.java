package br.com.muttley.security.server.service;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;

/**
 * @author Joel Rodrigues Moreira on 01/11/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
public interface UserPreferenceService {
    UserPreferences getPreferences(final User user);

    void setPreferences(final User user, final Preference preferences);

    void removePreference(final User user, final String key);
}
