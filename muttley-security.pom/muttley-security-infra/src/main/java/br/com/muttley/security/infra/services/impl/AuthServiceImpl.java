package br.com.muttley.security.infra.services.impl;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.infra.feign.UserPreferenceServiceClient;
import br.com.muttley.security.infra.feign.UserServiceClient;
import br.com.muttley.security.infra.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    protected UserServiceClient userServiceClient;
    protected final UserPreferenceServiceClient preferenceService;

    /**
     * Se tivermos em um contexto de microserviço, o tokenHeader deve ser Authorization-jwt, caso contrario
     * deve ser apenas Authorization
     */
    @Autowired
    public AuthServiceImpl(String tokenHeader, UserServiceClient userServiceClient, final UserPreferenceServiceClient preferenceService) {
        this.tokenHeader = tokenHeader;
        this.userServiceClient = userServiceClient;
        this.preferenceService = preferenceService;
    }

    @Override
    public Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public JwtUser getCurrentJwtUser() {
        return (JwtUser) getCurrentAuthentication().getPrincipal();
    }

    /**
     * Retorna o token corrente da requisição
     */
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
            final UserPreferences preferences = this.preferenceService.getPreferences(user.getId());
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

    /**
     * Retorna um usuário atravez do nome de usuário
     *
     * @param username pode ser um nome ou um email
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return new JwtUser(this.userServiceClient.findByUserName(username));
    }
}
