package br.com.muttley.security.server.service.impl;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.service.AuthService;
import br.com.muttley.security.server.service.UserPreferencesService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Joel Rodrigues Moreira on 19/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class AuthServiceImpl implements AuthService {

    protected final String tokenHeader;
    protected final UserService userService;
    protected final UserPreferencesService preferencesService;

    public AuthServiceImpl(@Value("${muttley.security.jwt.controller.tokenHeader-jwt:Authorization-jwt}") final String tokenHeader, final UserService userService, final UserPreferencesService preferencesService) {
        this.tokenHeader = tokenHeader;
        this.userService = userService;
        this.preferencesService = preferencesService;
    }

    @Override
    public Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public JwtUser getCurrentJwtUser() {
        return (JwtUser) getCurrentAuthentication().getPrincipal();
    }

    @Override
    public JwtToken getCurrentToken() {
        return new JwtToken(
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest()
                        .getHeader(this.tokenHeader)
        );
    }

    @Override
    public User getCurrentUser() {
        return this.userService.getUserFromToken(this.getCurrentToken());
        //return getCurrentJwtUser().getOriginUser();
    }

    @Override
    public UserPreferences getUserPreferences() {
        final User user = this.getCurrentUser();
        if (user.getPreferences() == null || user.getPreferences().isEmpty()) {
            final UserPreferences preferences = this.preferencesService.getUserPreferences(user);
            if (preferences != null) {
                user.setPreferences(preferences);
            }
        }
        return this.getCurrentUser().getPreferences();
    }

    @Override
    public Preference getPreference(final String key) {
        return this.preferencesService.getPreference(this.getCurrentUser(), key);
    }

}
