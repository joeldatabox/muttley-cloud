package br.com.muttley.security.server.listeners;

import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.events.OwnerCreateEvent;
import br.com.muttley.security.server.service.UserService;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static br.com.muttley.model.security.Role.ROLE_OWNER;
import static br.com.muttley.model.security.preference.UserPreferences.WORK_TEAM_PREFERENCE;

/**
 * @author Joel Rodrigues Moreira on 16/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Ao se criar um Owner no sistema, devemos garantir que seja criado um Grupo de trabalho
 * padrão para esse mesmo usuário
 */
@Component
public class OwnerCreateEventListener implements ApplicationListener<OwnerCreateEvent> {

    private final WorkTeamService service;
    private final UserService userService;

    @Autowired
    public OwnerCreateEventListener(final WorkTeamService service, final UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(final OwnerCreateEvent ownerCreateEvent) {
        final User userMaster = ownerCreateEvent.getSource().getUserMaster();
        final WorkTeam workTeam = this.service.save(
                userMaster,
                new WorkTeam()
                        .setName("Master")
                        .setDescription("Esse é o grupo principal")
                        .setOwner(ownerCreateEvent.getSource())
                        .setUserMaster(userMaster)
                        .addMember(userMaster)
                        .addRole(ROLE_OWNER)
        );

        /*Já que acabamos de criar um Owner, devemos verificar se o usuário master já tem algumas preferencias básicas
         * tudo isso para evitar erros
         */
        final UserPreferences preference = this.userService.loadPreference(userMaster);
        if (!preference.contains(WORK_TEAM_PREFERENCE)) {
            preference.set(WORK_TEAM_PREFERENCE, workTeam);
            //salvando as alterções das preferencias
            this.userService.save(userMaster, preference);
        }
    }
}
