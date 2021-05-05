package br.com.muttley.admin.server.listeners;

import br.com.muttley.admin.server.events.OwnerCreatedEvent;
import br.com.muttley.admin.server.service.AdminWorkTeamService;
import br.com.muttley.model.admin.AdminUserBase;
import br.com.muttley.model.admin.AdminWorkTeam;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static br.com.muttley.model.security.Role.ROLE_OWNER;

/**
 * @author Joel Rodrigues Moreira 27/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
//@Component
public class OwnerCreatedEventListeners implements ApplicationListener<OwnerCreatedEvent> {
    final AdminWorkTeamService workTeamService;
    //final br.com.muttley.security.server.service.AdminUserBaseService

    @Autowired
    public OwnerCreatedEventListeners(final AdminWorkTeamService workTeamService) {
        this.workTeamService = workTeamService;
    }

    @Override
    public void onApplicationEvent(final OwnerCreatedEvent event) {
        final User userMaster = event.getSource().getUserMaster();

        // Adicinando a base de usuário para esse novo owner cadastradao
        final UserBase userBase = new UserBase();
        userBase.setOwner(event.getSource())
                .addUser(userMaster, userMaster);

        //this.userBaseService.save(currentUser, event.getSource(), userBase);



        AdminWorkTeam workTeam = (AdminWorkTeam) new AdminWorkTeam()
                .setName("Master")
                .setDescription("Esse é o grupo principal")
                .setOwner(event.getSource())
                .setUserMaster(userMaster)
                .addMember(userMaster)
                .addRole(ROLE_OWNER);
        userMaster.setCurrentOwner(workTeam.getOwner());

        workTeam = this.workTeamService.save(userMaster, workTeam);

        /*Já que acabamos de criar um Owner, devemos verificar se o usuário master já tem algumas preferencias básicas
         * tudo isso para evitar erros
         */
        /*final UserPreferences preference = this.userService.loadPreference(userMaster);
        if (!preference.contains(OWNER_PREFERENCE)) {
            preference.set(OWNER_PREFERENCE, workTeam.getOwner());
            //salvando as alterções das preferencias
            this.userService.save(userMaster, preference);
        }*/


    }
}
