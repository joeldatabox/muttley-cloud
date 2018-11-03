package br.com.muttley.security.infra.services;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.UserPreferences;

/**
 * @author Joel Rodrigues Moreira on 02/11/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
public interface CacheUserPreferences {
    public UserPreferences get(final User user);

    public CacheUserPreferences set(final User user, UserPreferences userPreferences, long time);
}
