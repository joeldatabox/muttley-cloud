package br.com.muttley.admin.server.config.postint;

import br.com.muttley.admin.server.service.AdminUserBaseService;
import br.com.muttley.admin.server.service.NoSecurityAdminOwnerService;
import br.com.muttley.admin.server.service.NoSecurityAdminPassaportService;
import br.com.muttley.exception.throwables.MuttleyConflictException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.admin.AdminOwner;
import br.com.muttley.model.admin.AdminPassaport;
import br.com.muttley.model.admin.AdminUserBase;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBaseItem;
import br.com.muttley.model.security.UserPayLoad;
import br.com.muttley.model.security.preference.Foto;
import br.com.muttley.security.feign.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class UserConfig implements ApplicationListener<ApplicationReadyEvent> {
    private final String defaultUser;
    private final String userRead;
    private final String passwdDefaultUser;
    private final String passwdUserRead;
    private final String nameOrganization;
    private final UserServiceClient service;
    private final AdminUserBaseService adminUserBaseService;

    private final NoSecurityAdminOwnerService ownerService;
    private final NoSecurityAdminPassaportService passaportService;

    @Autowired
    public UserConfig(
            @Value("${muttley.admin-server.default-user}") String defaultUser,
            @Value("${muttley.admin-server.user-read:#{null}}") String userRead,
            @Value("${muttley.admin-server.passwd-default-user}") String passwdDefaultUser,
            @Value("${muttley.admin-server.passwd-user-read:#{null}}") String passwdUserRead,
            @Value("${muttley.admin-server.name-organization}") String nameOrganization,
            UserServiceClient service,
            AdminUserBaseService adminUserBaseService,
            NoSecurityAdminOwnerService ownerService,
            NoSecurityAdminPassaportService passaportService) {
        this.defaultUser = defaultUser;
        this.userRead = userRead;
        this.passwdDefaultUser = passwdDefaultUser;
        this.passwdUserRead = passwdUserRead;
        this.nameOrganization = nameOrganization;
        this.service = service;
        this.adminUserBaseService = adminUserBaseService;
        this.ownerService = ownerService;
        this.passaportService = passaportService;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        AdminOwner owner;
        try {
            owner = this.ownerService.findByName(this.nameOrganization);
        } catch (MuttleyNotFoundException var7) {
            User user = null;

            try {
                user = this.service.save(new UserPayLoad("Admin", "Usuário para administrar todo o ecossistema", this.defaultUser, (String) null, (Foto) null, (Set) null, this.passwdDefaultUser, (String) null, true, (String) null, (String) null, false), "true");
            } catch (MuttleyConflictException var6) {
                user = this.service.findByUserName(this.defaultUser);
            }

            owner = this.ownerService.save((AdminOwner) (new AdminOwner()).setName(this.nameOrganization).setDescription("Administrador unico do sistema").setUserMaster(user));
            this.adminUserBaseService.save(user, (AdminUserBase) (new AdminUserBase()).setOwner(owner).addUser((new UserBaseItem()).setUser(user).setAddedBy(user).setStatus(true).setDtCreate(new Date())));
            AdminPassaport var5 = this.passaportService.save((AdminPassaport) (new AdminPassaport()).setName("Grupo principal").setUserMaster(user).setOwner(owner).addRole(Role.ROLE_ROOT).setDescription("Não pode exister outro grupo no odin repository").addMember(user));
        }

        this.createUserRead(owner);
    }

    private void createUserRead(Owner owner) {
        if (!ObjectUtils.isEmpty(this.userRead) || !ObjectUtils.isEmpty(this.passwdUserRead)) {
            try {
                SecurityContext ctx = SecurityContextHolder.createEmptyContext();
                SecurityContextHolder.setContext(ctx);
                ctx.setAuthentication(new AnonymousAuthenticationToken(owner.getUserMaster().getId(), owner.getUserMaster(), Arrays.asList(new GrantedAuthority() {
                    @Override
                    public String getAuthority() {
                        return Role.ROLE_ROOT.toString();
                    }
                })));

                //Do what ever you want to do


                User userRead = null;

                try {
                    userRead = this.service.findByUserName(this.userRead);
                } catch (MuttleyNotFoundException var5) {
                }

                if (userRead == null) {
                    try {
                        userRead = this.service.save(new UserPayLoad("AdminRead", "Usuário para consumir dados do ecossistema", this.userRead, (String) null, (Foto) null, (Set) null, this.passwdUserRead, (String) null, true, (String) null, (String) null, false), "true");
                    } catch (MuttleyConflictException var4) {
                        userRead = this.service.findByUserName(this.userRead);
                    }

                    this.adminUserBaseService.update(owner.getUserMaster(), (AdminUserBase) ((AdminUserBase) this.adminUserBaseService.findFirst(owner.getUserMaster())).addUser((new UserBaseItem()).setUser(userRead).setAddedBy(userRead).setStatus(true).setDtCreate(new Date())));
                    AdminPassaport var3 = this.passaportService.save((AdminPassaport) (new AdminPassaport()).setName("Grupo principal para leitura").setUserMaster(owner.getUserMaster()).setOwner(owner).addRole(Role.ROLE_ROOT).setDescription("Não pode exister outro grupo no odin repository").addMember(userRead));
                }

            } finally {
                SecurityContextHolder.clearContext();
            }
        }
    }

}
