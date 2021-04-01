package br.com.muttley.security.infra.service.impl;

import br.com.muttley.localcache.services.LocalUserAuthenticationService;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.feign.UserDataBindingClient;
import br.com.muttley.security.feign.UserPreferenceServiceClient;
import br.com.muttley.security.infra.service.AuthService;
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
    protected final UserPreferenceServiceClient preferenceService;
    protected final UserDataBindingClient dataBindingService;
    protected final LocalUserAuthenticationService localUserAuthenticationService;

    public AuthServiceImpl(@Value("${muttley.security.jwt.controller.tokenHeader:Authorization}") final String tokenHeader, final UserPreferenceServiceClient preferenceService, final UserDataBindingClient dataBindingService, final LocalUserAuthenticationService localUserAuthenticationService) {
        this.tokenHeader = tokenHeader;
        this.preferenceService = preferenceService;
        this.dataBindingService = dataBindingService;
        this.localUserAuthenticationService = localUserAuthenticationService;
    }

    @Override
    public Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public JwtUser getCurrentJwtUser() {
        final Authentication authentication = this.getCurrentAuthentication();
        if (authentication != null) {
            return (JwtUser) authentication.getPrincipal();
        }
        return this.localUserAuthenticationService.getJwtUserFrom(getCurrentToken());
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
            final UserPreferences preferences = this.preferenceService.getUserPreferences();
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

}
