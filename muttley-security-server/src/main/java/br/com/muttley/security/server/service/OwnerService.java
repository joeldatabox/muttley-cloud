package br.com.muttley.security.server.service;

import br.com.muttley.domain.service.Service;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.User;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface OwnerService extends Service<Owner> {
    Owner findByName(final String name);

    /**
     * Retorna todos os owner estão lincado a um determinado usuário usando como base
     * a colection de UserBase
     */
    List<? extends OwnerData> loadOwnersOfUser(final User user);

    /**
     * Busca um owner pelo id que tenha vinculo com o usuário informado
     */
    Owner findByUserAndId(final User user, final String id);
}
