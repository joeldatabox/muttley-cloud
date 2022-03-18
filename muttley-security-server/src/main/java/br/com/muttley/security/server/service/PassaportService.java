package br.com.muttley.security.server.service;

import br.com.muttley.domain.service.Service;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.Passaport;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserData;
import br.com.muttley.model.security.rolesconfig.AvaliableRoles;

import java.util.List;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface PassaportService extends Service<Passaport> {
    Passaport findByName(final User user, final String name);

    List<Passaport> findByUserMaster(final Owner owner, final User user);

    List<Passaport> findByUser(final User user);

    Set<Role> loadCurrentRoles(final User user);

    AvaliableRoles loadAvaliableRoles(final User user);

    void removeUserFromAllPassaport(final Owner owner, final User user);

    void addUserForPassaportIfNotExists(final User user, final Passaport passaport, final UserData userForAdd);

    boolean userIsPresentInPassaport(final User user, final Passaport passaport, final UserData userForCheck);

    boolean userIsPresentInPassaport(final User user, final String idPassaport, final UserData userForCheck);

    /**
     * Método para
     */
    Passaport createPassaportFor(final User user, final String ownerId, final Passaport passaport);

    /**
     * Realiza as configurações
     */
    void configPassaports(final User user);
}
