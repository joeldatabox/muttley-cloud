package br.com.muttley.security.infra.component.authentication;

import br.com.muttley.localcache.services.LocalDatabindingService;
import br.com.muttley.localcache.services.LocalOwnerService;
import br.com.muttley.localcache.services.LocalRolesService;
import br.com.muttley.localcache.services.LocalUserPreferenceService;
import br.com.muttley.localcache.services.LocalWorkTeamService;
import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.XAPIToken;
import br.com.muttley.model.security.authentication.Authentication;
import br.com.muttley.model.security.events.authentication.AuthenticationAfterCacheLoadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static br.com.muttley.model.security.preference.UserPreferences.OWNER_PREFERENCE;

/**
 * @author Joel Rodrigues Moreira on 19/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class AuthenticationAfterCacheLoadEventListener implements ApplicationListener<AuthenticationAfterCacheLoadEvent> {
    private final LocalUserPreferenceService localUserPreferenceService;
    private final LocalOwnerService ownerService;
    private final LocalRolesService rolesService;
    private final LocalDatabindingService databindingService;
    private final LocalWorkTeamService localWorkTeamService;

    @Autowired
    public AuthenticationAfterCacheLoadEventListener(final LocalUserPreferenceService localUserPreferenceService, final LocalOwnerService ownerService, final LocalRolesService rolesService, final LocalDatabindingService databindingService, final LocalWorkTeamService localWorkTeamService) {
        this.localUserPreferenceService = localUserPreferenceService;
        this.ownerService = ownerService;
        this.rolesService = rolesService;
        this.databindingService = databindingService;
        this.localWorkTeamService = localWorkTeamService;
    }

    @Override
    public void onApplicationEvent(final AuthenticationAfterCacheLoadEvent event) {
        final XAPIToken token = event.getToken();
        final Authentication authentication = event.getAuthentication();
        //final JwtToken token = event.getJwtToken();
        //carregando preferencias
        authentication.setPreferences(this.localUserPreferenceService.getUserPreferences(token, authentication.getCurrentUser()));

        authentication.setAuthorities(this.rolesService.loadCurrentRoles(event.getToken(), event.getAuthentication().getCurrentUser()));
        authentication.setDataBindings(this.databindingService.getUserDataBindings(token, authentication.getCurrentUser()));
        authentication.setWorkTeam(this.localWorkTeamService.getWorkTeamDomain(token, authentication.getCurrentUser()));
    }
}
