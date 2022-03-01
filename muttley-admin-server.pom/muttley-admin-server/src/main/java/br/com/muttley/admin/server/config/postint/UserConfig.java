package br.com.muttley.admin.server.config.postint;

import br.com.muttley.admin.server.service.AdminUserBaseService;
import br.com.muttley.admin.server.service.NoSecurityAdminOwnerService;
import br.com.muttley.admin.server.service.NoSecurityAdminPassaportService;
import br.com.muttley.exception.throwables.MuttleyConflictException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.admin.AdminOwner;
import br.com.muttley.model.admin.AdminUserBase;
import br.com.muttley.model.admin.AdminPassaport;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBaseItem;
import br.com.muttley.model.security.UserPayLoad;
import br.com.muttley.security.feign.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class UserConfig implements ApplicationListener<ApplicationReadyEvent> {
    private final String defaultUser;
    private final String passwdDefaultUser;
    private final String nameOrganization;
    private final UserServiceClient service;
    private final AdminUserBaseService adminUserBaseService;

    private final NoSecurityAdminOwnerService ownerService;
    private final NoSecurityAdminPassaportService passaportService;

    @Autowired
    public UserConfig(
            @Value("${muttley.admin-server.default-user}") String defaultUser,
            @Value("${muttley.admin-server.passwd-default-user}") String passwdDefaultUser,
            @Value("${muttley.admin-server.name-organization}") String nameOrganization,
            UserServiceClient service,
            AdminUserBaseService adminUserBaseService,
            NoSecurityAdminOwnerService ownerService,
            NoSecurityAdminPassaportService passaportService) {
        this.defaultUser = defaultUser;
        this.passwdDefaultUser = passwdDefaultUser;
        this.nameOrganization = nameOrganization;
        this.service = service;
        this.adminUserBaseService = adminUserBaseService;
        this.ownerService = ownerService;
        this.passaportService = passaportService;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        try {
            this.ownerService.findByName(this.nameOrganization);
        } catch (MuttleyNotFoundException ex) {
            //criando usuário
            User user = null;
            try {
                user = this.service.save(new UserPayLoad("Admin", "Usuário para administrar todo o ecossistema", this.defaultUser, null, null, this.passwdDefaultUser), "true");
            } catch (MuttleyConflictException e) {
                user = this.service.findByUserName(this.defaultUser);
            }

            //criando o Owner principal;
            final AdminOwner owner = this.ownerService.save(
                    (AdminOwner) new AdminOwner()
                            .setName(this.nameOrganization)
                            .setDescription("Administrador unico do sistema")
                            .setUserMaster(user)
            );

            this.adminUserBaseService.save(user, (AdminUserBase) new AdminUserBase().setOwner(owner).addUser(new UserBaseItem().setUser(user).setAddedBy(user).setStatus(true).setDtCreate(new Date())));


            //criando o grupo de trabalho
            final AdminPassaport passaport =
                    this.passaportService.save(
                            (AdminPassaport) new AdminPassaport()
                                    .setName("Grupo principal")
                                    .setUserMaster(user)
                                    .setOwner(owner)
                                    .addRole(Role.ROLE_ROOT)
                                    .setDescription("Não pode exister outro grupo no odin repository")
                                    .addMember(user)
                    );

            //criando preferencia de grupo de trabalho
            /*final UserPreferences preferences = this.preferenceServiceClient.getPreferences(user.getId());
            preferences.set(new Preference(UserPreferences.WORK_TEAM_PREFERENCE, workTeam.getId()));*/
            //this.preferenceServiceClient.setPreference(new Preference(UserPreferences.OWNER_PREFERENCE, workTeam.getOwner().getId()));
        }
    }

}
