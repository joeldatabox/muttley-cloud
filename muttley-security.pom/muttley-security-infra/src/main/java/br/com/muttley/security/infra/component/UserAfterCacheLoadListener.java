package br.com.muttley.security.infra.component;

import br.com.muttley.metadata.headers.HeaderAuthorizationJWT;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.UserAfterCacheLoadEvent;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.infra.feign.UserPreferenceServiceClient;
import br.com.muttley.security.infra.feign.WorkTeamServiceClient;
import br.com.muttley.security.infra.services.CacheUserPreferences;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 17/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class UserAfterCacheLoadListener implements ApplicationListener<UserAfterCacheLoadEvent> {
    private final UserPreferenceServiceClient preferenceServiceClient;
    private final WorkTeamServiceClient workteamService;
    private final CacheUserPreferences cacheUserPreferences;
    @Autowired
    private HeaderAuthorizationJWT authorizationJWT;

    @Autowired
    public UserAfterCacheLoadListener(final UserPreferenceServiceClient preferenceServiceClient, final WorkTeamServiceClient workteamService, final CacheUserPreferences cacheUserPreferences) {
        this.preferenceServiceClient = preferenceServiceClient;
        this.workteamService = workteamService;
        this.cacheUserPreferences = cacheUserPreferences;
    }

    @Override
    public void onApplicationEvent(final UserAfterCacheLoadEvent event) {
        final User user = event.getSource();

        //carregando preferencias
        //verificando se tem algum cache
        UserPreferences preferences = this.cacheUserPreferences.get(user);

        if (preferences == null) {
            //se não existe cache, devemos buscar no servidor de segurança
            preferences = preferenceServiceClient.getPreferences();
            //salvando as preferencias carregadas no cache
            this.cacheUserPreferences.set(user, preferences, authorizationJWT.getToken().getExpiration());
        }
        final ObjectId idWorkTeam = new ObjectId(preferences.get(UserPreferences.WORK_TEAM_PREFERENCE).getValue().toString());
        user.setPreferences(preferences);
        user.setCurrentWorkTeam(workteamService.findById(idWorkTeam.toString()));
    }
}
