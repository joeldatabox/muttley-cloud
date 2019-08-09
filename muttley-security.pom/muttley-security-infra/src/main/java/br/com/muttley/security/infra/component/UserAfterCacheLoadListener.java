package br.com.muttley.security.infra.component;

import br.com.muttley.metadata.headers.HeaderAuthorizationJWT;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.model.security.events.UserAfterCacheLoadEvent;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.infra.feign.UserPreferenceServiceClient;
import br.com.muttley.security.infra.feign.WorkTeamServiceClient;
import br.com.muttley.security.infra.services.CacheUserPreferences;
import br.com.muttley.security.infra.services.CacheWorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static br.com.muttley.model.security.preference.UserPreferences.WORK_TEAM_PREFERENCE;

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
    private final CacheWorkTeamService cacheWorkTeamService;
    @Autowired
    private HeaderAuthorizationJWT authorizationJWT;

    @Autowired
    public UserAfterCacheLoadListener(final UserPreferenceServiceClient preferenceServiceClient, final WorkTeamServiceClient workteamService, final CacheUserPreferences cacheUserPreferences, final CacheWorkTeamService cacheWorkTeamService) {
        this.preferenceServiceClient = preferenceServiceClient;
        this.workteamService = workteamService;
        this.cacheUserPreferences = cacheUserPreferences;
        this.cacheWorkTeamService = cacheWorkTeamService;
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

        if (preferences.contains(WORK_TEAM_PREFERENCE)) {
            //carregando o workteam
            //verificando se tem algum cache
            final String idWorkTeam = preferences.get(WORK_TEAM_PREFERENCE).getValue().toString();
            WorkTeam workTeam = this.cacheWorkTeamService.get(user, idWorkTeam);
            if (workTeam == null) {
                //se não existe cache, devemos buscar no servidor de segurança
                workTeam = workteamService.findById(idWorkTeam);
                //salvando o workteam carregado no cache
                this.cacheWorkTeamService.set(user, workTeam, authorizationJWT.getToken().getExpiration());
            }

            user.setCurrentWorkTeam(workTeam);
            user.setAuthorities(workTeam.getRoles());
        } else {
            //buscando qualquer workteam
            final List<WorkTeam> itens = workteamService.findByUser();
            final Preference preference = new Preference(WORK_TEAM_PREFERENCE, itens.get(0).getId());
            preferences.set(preference);

            preferenceServiceClient.setPreferenceByUserName(user.getUserName(), preference);
            //salvando o workteam carregado no cache
            this.cacheWorkTeamService.set(user, itens.get(0), authorizationJWT.getToken().getExpiration());
            //atualizando o cache local
            this.cacheUserPreferences.set(user, preferences, authorizationJWT.getToken().getExpiration());
            user.setCurrentWorkTeam(itens.get(0));
            user.setAuthorities(itens.get(0).getRoles());
        }

        user.setPreferences(preferences);

    }
}
