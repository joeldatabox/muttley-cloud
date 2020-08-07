package br.com.muttley.security.server.service;

import br.com.muttley.model.security.AccessPlan;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface AccessPlanService extends SecurityService<AccessPlan> {
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
    AccessPlan findByDescription(String descricao);
}
