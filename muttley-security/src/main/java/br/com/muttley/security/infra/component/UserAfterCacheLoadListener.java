package br.com.muttley.security.infra.component;

import br.com.muttley.localcache.services.LocalDatabindingService;
import br.com.muttley.localcache.services.LocalOwnerService;
import br.com.muttley.localcache.services.LocalRolesService;
import br.com.muttley.localcache.services.LocalUserPreferenceService;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.UserAfterCacheLoadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static br.com.muttley.model.security.preference.UserPreferences.OWNER_PREFERENCE;

/**
 * @author Joel Rodrigues Moreira on 17/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class UserAfterCacheLoadListener implements ApplicationListener<UserAfterCacheLoadEvent> {

    private final LocalUserPreferenceService localUserPreferenceService;
    private final LocalOwnerService ownerService;
    private final LocalRolesService rolesService;
    private final LocalDatabindingService databindingService;

    @Autowired
    public UserAfterCacheLoadListener(final LocalUserPreferenceService localUserPreferenceService, final LocalOwnerService ownerService, final LocalRolesService rolesService, final LocalDatabindingService databindingService) {
        this.localUserPreferenceService = localUserPreferenceService;
        this.ownerService = ownerService;
        this.rolesService = rolesService;
        this.databindingService = databindingService;
    }

    @Override
    public void onApplicationEvent(final UserAfterCacheLoadEvent event) {
        final User user = event.getUser();
        final JwtToken token = event.getJwtToken();
        //carregando preferencias
        user.setPreferences(this.localUserPreferenceService.getUserPreferences(token, user));
        //setando o owner
        user.setCurrentOwner((OwnerData) user.getPreferences().get(OWNER_PREFERENCE).getResolved());
        user.setAuthorities(this.rolesService.loadCurrentRoles(event.getJwtToken(), event.getUser()));
        user.setDataBindings(this.databindingService.getUserDataBindings(token, user));

        /*final List<UserDataBinding> dataBindings = dataBindingService.list();
        try {
            if (!preferences.contains(OWNER_PREFERENCE)) {
                final OwnerData owner = this.ownerService.loadOwnerAny();
                //salvando o owner nas preferencias
                final Preference preference = new Preference(OWNER_PREFERENCE, owner.getId()).setResolved(owner);
                preferences.set(preference);
                preferenceService.setPreference(preference);
                //setando o owner atual
                user.setCurrentOwner(owner);
                //carregando authorities
                //user.setAuthorities(this.workTeamService.loadCurrentRoles());
                this.loadRoles(user);
                user.setDataBindings(dataBindings);
            } else {
                final ObjectId idOwner = new ObjectId(preferences.get(OWNER_PREFERENCE).getValue().toString());
                user.setPreferences(preferences);
                user.setDataBindings(dataBindings);
                final Owner owner = ownerService.findByUserAndId(idOwner.toString());
                user.setCurrentOwner(owner);
                //carregando authorities
                //user.setAuthorities(this.workTeamService.loadCurrentRoles());
                this.loadRoles(user);
            }
        } catch (MuttleyNotFoundException ex) {
            throw new MuttleySecurityCredentialException("Não foi possível recuperar informações do seu usuáiro");
        }*/
    }
/*
    private void loadRoles(final User user) {
        try {
            user.setAuthorities(this.workTeamService.loadCurrentRoles());
        } catch (final MuttleyNotFoundException ex) {
        }
    }*/
}
