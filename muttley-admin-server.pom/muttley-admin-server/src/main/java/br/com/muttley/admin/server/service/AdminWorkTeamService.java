package br.com.muttley.admin.server.service;

import br.com.muttley.domain.service.Service;
import br.com.muttley.model.admin.AdminOwner;
import br.com.muttley.model.admin.AdminPassaport;
import br.com.muttley.model.security.User;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira 20/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface AdminWorkTeamService extends Service<AdminPassaport> {
    AdminPassaport findById1(final User user, final String id);

    @PreAuthorize(
            "this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles())" +
                    "   )" +
                    "): " +
                    "   true"
    )
    AdminPassaport findByName(final AdminOwner owner, final String nome);

    @PreAuthorize(
            "this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles())" +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true"
    )
    List<AdminPassaport> loadAllWorkTeams(final User user);

    void removeUserFromAllWorkTeam(AdminOwner owner, User user);
}
