package br.com.muttley.security.server.service.impl;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.service.AuthService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Joel Rodrigues Moreira on 22/07/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
//@Service
public class AuthServiceImpl implements AuthService {

    protected final String tokenHeader;
    protected final UserService userService;

    public AuthServiceImpl(@Value("${muttley.security.jwt.controller.tokenHeader-jwt:Authorization-jwt}") final String tokenHeader, final UserService userService) {
        this.tokenHeader = tokenHeader;
        this.userService = userService;
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
        return getCurrentJwtUser().getOriginUser();
    }

    @Override
    public UserPreferences getUserPreferences() {
        final User user = this.getCurrentUser();
        if (user.getPreferences() == null || user.getPreferences().isEmpty()) {
            final UserPreferences preferences = this.userService.loadPreference(user);
            if (preferences != null) {
                user.setPreferences(preferences);
            }
        }
        return this.getCurrentUser().getPreferences();
    }

    @Override
    public Preference getPreference(final String key) {
        return this.getUserPreferences().get(key);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return new JwtUser(new User());
    }
}
