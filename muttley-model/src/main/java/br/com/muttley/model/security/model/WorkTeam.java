package br.com.muttley.model.security.model;

import br.com.muttley.model.Owner;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 28/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Representa o time de trabalho ou um grupo de usuário
 */
public interface WorkTeam {

    String getName();

    String getDescription();

    User getUserMaster();

    Owner getOwner();

    Set<User> getMembers();

    Set<Authority> getAuthorities();

}
