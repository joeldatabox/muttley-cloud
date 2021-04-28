package br.com.muttley.admin.server.service;

import br.com.muttley.domain.service.Service;
import br.com.muttley.model.admin.AdminOwner;
import br.com.muttley.model.security.User;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author Joel Rodrigues Moreira 20/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface AdminOwnerService extends Service<AdminOwner> {
    @PreAuthorize(
            "this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" + "   ) " +
                    "): " +
                    "   true"
    )
    AdminOwner findByName(final User user, final String name);

    AdminOwner findById1(final User user, final String id);
}

