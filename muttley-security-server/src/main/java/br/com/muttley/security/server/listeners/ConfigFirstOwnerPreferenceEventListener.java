package br.com.muttley.security.server.listeners;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.events.ConfigFirstOwnerPreferenceEvent;
import br.com.muttley.security.server.service.UserService;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static br.com.muttley.model.security.preference.UserPreferences.OWNER_PREFERENCE;

/**
 * @author Joel Rodrigues Moreira on 16/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * <p>
 * Talvez o usuário da requisição não tenha um owner configurado por padrão ou esteja com as preferencias vazias,
 * levando isso em consideração esse serviço é disparado para se configurar opções básicas necessárias
 */
@Component
public class ConfigFirstOwnerPreferenceEventListener implements ApplicationListener<ConfigFirstOwnerPreferenceEvent> {
    private final UserService service;
    private final WorkTeamService workTeamService;

    @Autowired
    public ConfigFirstOwnerPreferenceEventListener(final UserService service, final WorkTeamService workTeamService) {
        this.service = service;
        this.workTeamService = workTeamService;
    }

    @Override
    public void onApplicationEvent(final ConfigFirstOwnerPreferenceEvent configFirst) {
        final User user = configFirst.getSource();

        final UserPreferences preference = this.service.loadPreference(user);
        if (!preference.contains(OWNER_PREFERENCE)) {
            //setando o owner do primeiro workteam que encontrar
            final WorkTeam workTeam = this.workTeamService.findByUser(user).get(0);
            preference.set(OWNER_PREFERENCE, workTeam.getOwner());
            //salvando as alterções das preferencias
            this.service.save(user, preference);
            user.setPreferences(preference);
            user.setCurrentOwner(workTeam.getOwner());
        }
    }
}