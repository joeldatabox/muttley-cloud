package br.com.muttley.security.server.listeners;

import br.com.muttley.model.security.Passaport;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBase;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.events.OwnerCreateEvent;
import br.com.muttley.security.server.service.AuthService;
import br.com.muttley.security.server.service.PassaportService;
import br.com.muttley.security.server.service.UserBaseService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static br.com.muttley.model.security.Role.ROLE_OWNER;
import static br.com.muttley.model.security.preference.UserPreferences.OWNER_PREFERENCE;

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

    private final PassaportService service;
    private final UserService userService;
    private final UserBaseService userBaseService;
    private final AuthService authService;

    @Autowired
    public OwnerCreateEventListener(final PassaportService service, final UserService userService, final UserBaseService userBaseService, final AuthService authService) {
        this.service = service;
        this.userService = userService;
        this.userBaseService = userBaseService;
        this.authService = authService;
    }

    @Override
    public void onApplicationEvent(final OwnerCreateEvent ownerCreateEvent) {
        final User userMaster = ownerCreateEvent.getSource().getUserMaster();
        Passaport passaport = new Passaport()
                .setName("Master")
                .setDescription("Esse é o grupo principal")
                .setOwner(ownerCreateEvent.getSource())
                .setUserMaster(userMaster)
                .addMember(userMaster)
                .addRole(ROLE_OWNER);
        userMaster.setCurrentOwner(passaport.getOwner());

        passaport = this.service.save(userMaster, passaport);

        /*Já que acabamos de criar um Owner, devemos verificar se o usuário master já tem algumas preferencias básicas
         * tudo isso para evitar erros
         */
        final UserPreferences preference = this.userService.loadPreference(userMaster);
        if (!preference.contains(OWNER_PREFERENCE)) {
            preference.set(OWNER_PREFERENCE, passaport.getOwner());
            //salvando as alterções das preferencias
            this.userService.save(userMaster, preference);
        }

        // Adicinando a base de usuário para esse novo owner cadastradao
        final UserBase userBase = new UserBase();
        final User currentUser = this.userService.getUserFromToken(this.authService.getCurrentToken());
        userBase.setOwner(ownerCreateEvent.getSource())
                .addUser(userMaster, userMaster);

        this.userBaseService.save(currentUser, ownerCreateEvent.getSource(), userBase);
    }
}
