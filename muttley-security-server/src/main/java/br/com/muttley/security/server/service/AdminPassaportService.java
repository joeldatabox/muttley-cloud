package br.com.muttley.security.server.service;

import br.com.muttley.domain.service.Service;
import br.com.muttley.model.admin.AdminOwner;
import br.com.muttley.model.admin.AdminPassaport;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.rolesconfig.AvaliableRoles;

import java.util.List;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface AdminPassaportService extends Service<AdminPassaport> {
    AdminPassaport findByName(final User user, final String name);

    List<AdminPassaport> findByUserMaster(final AdminOwner owner, final User user);

    List<AdminPassaport> findByUser(final User user);

    Set<Role> loadCurrentRoles(final User user);

    AvaliableRoles loadAvaliableRoles(final User user);

    void removeUserFromAllPassaport(final AdminOwner owner, final User user);

    /**
     * Método para
     */
    AdminPassaport createPassaportFor(final User user, final String ownerId, final AdminPassaport passaport);

    /**
     * Realiza as configurações
     */
    void configPassaports(final User user);
}
