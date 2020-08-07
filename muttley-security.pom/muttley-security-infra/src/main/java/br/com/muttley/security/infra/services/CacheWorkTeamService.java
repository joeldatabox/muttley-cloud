package br.com.muttley.security.infra.services;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.WorkTeam;

/**
 * @author Joel Rodrigues Moreira on 02/11/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
public interface CacheWorkTeamService {
    public WorkTeam get(final User user, final String idWorkTeam);

    public CacheWorkTeamService set(final User user, WorkTeam workTeam, long time);
}
