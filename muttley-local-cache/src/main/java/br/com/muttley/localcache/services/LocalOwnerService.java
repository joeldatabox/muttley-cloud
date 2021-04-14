package br.com.muttley.localcache.services;

import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.User;

/**
 * @author Joel Rodrigues Moreira 25/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Seviço responsável recuperar owners e fazer cache em tempo de requisição de usuário autenticados
 */
public interface LocalOwnerService {
    public static final String BASIC_KEY = "OWNER:";

    OwnerData loadOwnerAny();

    OwnerData loadOwnerAny(final User user);

    OwnerData loadOwnerById(final String id);

    OwnerData loadOwnerById(final User user, final String id);
}
