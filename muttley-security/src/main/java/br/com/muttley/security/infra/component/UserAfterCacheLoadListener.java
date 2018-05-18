package br.com.muttley.security.infra.component;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.UserAfterCacheLoadEvent;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.feign.UserPreferenceServiceClient;
import br.com.muttley.security.feign.WorkTeamServiceClient;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

/**
 * @author Joel Rodrigues Moreira on 17/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class UserAfterCacheLoadListener implements ApplicationListener<UserAfterCacheLoadEvent> {

    private final UserPreferenceServiceClient preferenceServiceClient;
    private final WorkTeamServiceClient workteamService;

    @Autowired
    public UserAfterCacheLoadListener(final UserPreferenceServiceClient preferenceServiceClient, final WorkTeamServiceClient workteamService) {
        this.preferenceServiceClient = preferenceServiceClient;
        this.workteamService = workteamService;
    }

    @Override
    public void onApplicationEvent(final UserAfterCacheLoadEvent event) {
        final User user = event.getSource();
        //carregando preferencias
        final UserPreferences preferences = preferenceServiceClient.getPreferences(user.getId());
        final ObjectId idWorkTeam = new ObjectId(preferences.get(UserPreferences.WORK_TEAM_PREFERENCE).getValue().toString());
        user.setPreferences(preferences);
        user.setCurrentWorkTeam(workteamService.findById(idWorkTeam.toString()));
    }
}
