package br.com.muttley.security.server.service;

import br.com.muttley.model.Document;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;

/**
 * @author Joel Rodrigues Moreira 08/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface UserPreferencesService {

    UserPreferences createPreferencesFor(final User user);

    void save(final User user, final UserPreferences preferences);

    void setPreference(final User user, final Preference preference);

    void setPreference(final User user, final String key, final String value);

    void setPreference(final User user, final String key, final Document value);

    Preference getPreference(final User user, final String key);

    String getPreferenceValue(final User user, final String key);

    void removePreference(final User user, final String key);

    UserPreferences getUserPreferences(final User user);

    boolean containsPreference(final User user, final String key);
}
