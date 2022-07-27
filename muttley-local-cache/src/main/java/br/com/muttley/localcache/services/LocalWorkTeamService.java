package br.com.muttley.localcache.services;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.workteam.WorkTeamDomain;

/**
 * @author Joel Rodrigues Moreira on 21/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface LocalWorkTeamService {
    public static final String BASIC_KEY = "WORKTEAM:";

    public WorkTeamDomain getWorkTeamDomain(final JwtToken token, final User user);

    public LocalWorkTeamService expire(final User user);

    public LocalWorkTeamService expireByOwner(final User user);

    public static String getBasicKey(final Owner owner, final User user) {
        return getBasicKeyExpressionOwner(owner) + user.getId();
    }

    public static String getBasicKeyExpressionOwner(final Owner owner) {
        return BASIC_KEY + owner.getId() + ":";
    }
}
