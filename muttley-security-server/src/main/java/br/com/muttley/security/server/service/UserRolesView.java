package br.com.muttley.security.server.service;

import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 23/05/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface UserRolesView {
    Set<Role> findByUser(User user);
}
