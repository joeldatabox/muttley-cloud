package br.com.muttley.admin.server.security.listener;

import br.com.muttley.admin.server.service.AdminOwnerService;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.UserAfterCacheLoadEvent;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.feign.UserPreferenceServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class UserAfterCacheLoadListener implements ApplicationListener<UserAfterCacheLoadEvent> {

    private final UserPreferenceServiceClient preferenceServiceClient;
    private final AdminOwnerService adminOwnerService;

    @Autowired
    public UserAfterCacheLoadListener(final UserPreferenceServiceClient preferenceServiceClient, final AdminOwnerService adminOwnerService) {
        this.preferenceServiceClient = preferenceServiceClient;
        this.adminOwnerService = adminOwnerService;
    }

    @Override
    public void onApplicationEvent(final UserAfterCacheLoadEvent event) {
        final User user = event.getUser();
        //carregando preferencias
        final UserPreferences preferences = preferenceServiceClient.getUserPreferences();
        final String idPassaport = (String) preferences.get(UserPreferences.OWNER_PREFERENCE).getValue();
        user.setPreferences(preferences);
        user.setCurrentOwner(adminOwnerService.findById1(user, idPassaport));
    }
}
