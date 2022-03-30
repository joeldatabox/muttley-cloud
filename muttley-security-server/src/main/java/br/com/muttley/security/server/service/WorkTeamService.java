package br.com.muttley.security.server.service;

import br.com.muttley.domain.service.Service;
import br.com.muttley.model.security.Passaport;
import br.com.muttley.model.security.User;
import br.com.muttley.model.workteam.WorkTeam;
import br.com.muttley.model.workteam.WorkTeamDomain;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 03/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface WorkTeamService extends Service<WorkTeam> {
    WorkTeamDomain loadDomain(final User user);

    List<WorkTeam> findByUser(final User user);
}
