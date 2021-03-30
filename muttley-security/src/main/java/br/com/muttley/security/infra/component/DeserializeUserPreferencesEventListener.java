package br.com.muttley.security.infra.component;

import br.com.muttley.localcache.services.LocalOwnerService;
import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.events.DeserializeUserPreferencesEvent;
import br.com.muttley.model.security.preference.Preference;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static br.com.muttley.model.security.preference.UserPreferences.OWNER_PREFERENCE;

/**
 * @author Joel Rodrigues Moreira 25/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Esse lister recupera apenas as preferencias básica necessárias como no caso do onwer
 */
@Component
public class DeserializeUserPreferencesEventListener implements ApplicationListener<DeserializeUserPreferencesEvent> {
    private final LocalOwnerService ownerService;

    public DeserializeUserPreferencesEventListener(final LocalOwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @Override
    public void onApplicationEvent(final DeserializeUserPreferencesEvent event) {
        if (event.getPreferences().contains(OWNER_PREFERENCE)) {
            final Preference preference = event.getPreferences().get(OWNER_PREFERENCE);
            if (!preference.isResolved()) {
                final OwnerData ownerData = ownerService.loadOwnerById(preference.getValue().toString());
                preference.setResolved(ownerData);
                event.getUser().setCurrentOwner(ownerData);
            }
        } else {
            final OwnerData ownerData = ownerService.loadOwnerAny();
            final Preference preference = new Preference(OWNER_PREFERENCE, ownerData.getId());
            preference.setResolved(ownerData);
            event.getUser().setCurrentOwner(ownerData);
        }
    }
}
