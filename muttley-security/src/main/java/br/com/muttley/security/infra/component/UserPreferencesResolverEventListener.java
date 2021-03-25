package br.com.muttley.security.infra.component;

import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.events.UserPreferencesResolverEvent;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.security.infra.service.LocalOwnerService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static br.com.muttley.model.security.preference.UserPreferences.OWNER_PREFERENCE;

/**
 * @author Joel Rodrigues Moreira 25/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 *
 * Esse lister recupera apenas as preferencias básica necessárias como no caso do onwer
 */
@Component
public class UserPreferencesResolverEventListener implements ApplicationListener<UserPreferencesResolverEvent> {
    private final LocalOwnerService ownerService;

    public UserPreferencesResolverEventListener(final LocalOwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @Override
    public void onApplicationEvent(final UserPreferencesResolverEvent event) {
        if (event.getPreferences().contains(OWNER_PREFERENCE)) {
            final Preference preference = event.getPreferences().get(OWNER_PREFERENCE);
            if (!preference.isResolved()) {
                preference.setResolved(ownerService.loadOwnerById(preference.getValue().toString()));
            }
        } else {
            final OwnerData owner = ownerService.loadOwnerAny();
            final Preference preference = new Preference(OWNER_PREFERENCE, owner.getId());
            preference.setResolved(owner);
        }
    }
}
