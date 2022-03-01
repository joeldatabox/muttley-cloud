package br.com.muttley.admin.server.listeners;

import br.com.muttley.admin.server.events.OwnerCreatedEvent;
import br.com.muttley.admin.server.service.AdminPassaportService;
import br.com.muttley.model.admin.AdminPassaport;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import static br.com.muttley.model.security.Role.ROLE_OWNER;

/**
 * @author Joel Rodrigues Moreira 27/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
//@Component
public class OwnerCreatedEventListeners implements ApplicationListener<OwnerCreatedEvent> {
    final AdminPassaportService passaportService;
    //final br.com.muttley.security.server.service.AdminUserBaseService

    @Autowired
    public OwnerCreatedEventListeners(final AdminPassaportService passaportService) {
        this.passaportService = passaportService;
    }

    @Override
    public void onApplicationEvent(final OwnerCreatedEvent event) {
        final User userMaster = event.getSource().getUserMaster();

        // Adicinando a base de usuário para esse novo owner cadastradao
        final UserBase userBase = new UserBase();
        userBase.setOwner(event.getSource())
                .addUser(userMaster, userMaster);

        //this.userBaseService.save(currentUser, event.getSource(), userBase);


        AdminPassaport passaport = (AdminPassaport) new AdminPassaport()
                .setName("Master")
                .setDescription("Esse é o grupo principal")
                .setOwner(event.getSource())
                .setUserMaster(userMaster)
                .addMember(userMaster)
                .addRole(ROLE_OWNER);
        userMaster.setCurrentOwner(passaport.getOwner());

        passaport = this.passaportService.save(userMaster, passaport);

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
