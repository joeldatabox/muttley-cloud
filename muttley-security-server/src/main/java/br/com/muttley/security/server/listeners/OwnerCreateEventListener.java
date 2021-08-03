package br.com.muttley.security.server.listeners;

import br.com.muttley.model.events.OwnerCreateEvent;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBase;
import br.com.muttley.model.security.UserView;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.service.AuthService;
import br.com.muttley.security.server.service.UserBaseService;
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
    private final UserBaseService userBaseService;
    private final AuthService authService;

    @Autowired
    public OwnerCreateEventListener(final WorkTeamService service, final UserService userService, UserBaseService userBaseService, AuthService authService) {
        this.service = service;
        this.userService = userService;
        this.userBaseService = userBaseService;
        this.authService = authService;
    }

    @Override
    public void onApplicationEvent(final OwnerCreateEvent ownerCreateEvent) {
        final User userMaster = ownerCreateEvent.getSource().getUserMaster();
        WorkTeam workTeam = new WorkTeam()
                .setName("Master")
                .setDescription("Esse é o grupo principal")
                .setOwner(ownerCreateEvent.getSource())
                .setUserMaster(userMaster)
                .addMember(userMaster)
                .addRole(ROLE_OWNER);
        userMaster.setCurrentWorkTeam(workTeam);

        workTeam = this.service.save(userMaster, workTeam);

        /*Já que acabamos de criar um Owner, devemos verificar se o usuário master já tem algumas preferencias básicas
         * tudo isso para evitar erros
         */
        final UserPreferences preference = this.userService.loadPreference(userMaster);
        if (!preference.contains(WORK_TEAM_PREFERENCE)) {
            preference.set(WORK_TEAM_PREFERENCE, workTeam);
            //salvando as alterções das preferencias
            this.userService.save(userMaster, preference);
        }


        // Adicinando a base de usuário para esse novo owner cadastradao
        final UserBase userBase = new UserBase();
        userBase.setOwner(ownerCreateEvent.getSource())
                .addUser(new UserView(this.userService.getUserFromToken(this.authService.getCurrentToken())), new UserView(userMaster));

        this.userBaseService.save(this.userService.getUserFromToken(this.authService.getCurrentToken()), userBase);
    }
}
