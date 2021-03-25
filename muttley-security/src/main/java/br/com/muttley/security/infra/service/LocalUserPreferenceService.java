package br.com.muttley.security.infra.service;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.UserPreferences;

/**
 * @author Joel Rodrigues Moreira 24/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface LocalUserPreferenceService {
    public static final String BASIC_KEY = "USER-PREFENCES:";

    UserPreferences getUserPreferences(final JwtToken jwtUser, final User user);

    void expireUserPreferences(final User user);
}
