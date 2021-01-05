package br.com.muttley.security.infra.component;

import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.exception.throwables.security.MuttleySecurityCredentialException;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.UserAfterCacheLoadEvent;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.feign.OwnerServiceClient;
import br.com.muttley.security.feign.UserPreferenceServiceClient;
import br.com.muttley.security.feign.WorkTeamServiceClient;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static br.com.muttley.model.security.preference.UserPreferences.OWNER_PREFERENCE;

/**
 * @author Joel Rodrigues Moreira on 17/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class UserAfterCacheLoadListener implements ApplicationListener<UserAfterCacheLoadEvent> {

    private final UserPreferenceServiceClient preferenceService;
    private final OwnerServiceClient ownerService;
    private final WorkTeamServiceClient workTeamService;

    @Autowired
    public UserAfterCacheLoadListener(final UserPreferenceServiceClient preferenceService, final OwnerServiceClient ownerService, final WorkTeamServiceClient workTeamService) {
        this.preferenceService = preferenceService;
        this.ownerService = ownerService;
        this.workTeamService = workTeamService;
    }

    @Override
    public void onApplicationEvent(final UserAfterCacheLoadEvent event) {
        final User user = event.getSource();
        //carregando preferencias
        final UserPreferences preferences = preferenceService.getPreferences(user.getId());
        try {
            if (!preferences.contains(OWNER_PREFERENCE)) {
                final List<Owner> owners = this.ownerService.findByUser();
                //final List<WorkTeam> itens = workteamService.findByUser();
                final Preference preference = new Preference(OWNER_PREFERENCE, owners.get(0).getId());
                preferences.set(preference);
                preferenceService.setPreference(user.getId(), preference);

                user.setCurrentOwner(owners.get(0));
                //carregando authorities
                user.setAuthorities(this.workTeamService.loadCurrentRoles());
            } else {
                final ObjectId idOwner = new ObjectId(preferences.get(OWNER_PREFERENCE).getValue().toString());
                user.setPreferences(preferences);
                final Owner owner = ownerService.findByUserAndId(idOwner.toString());
                user.setCurrentOwner(owner);
                //carregando authorities
                user.setAuthorities(this.workTeamService.loadCurrentRoles());
            }
        } catch (MuttleyNotFoundException ex) {
            throw new MuttleySecurityCredentialException("Não foi possível recuperar informações do seu usuáiro");
        }
    }
}
