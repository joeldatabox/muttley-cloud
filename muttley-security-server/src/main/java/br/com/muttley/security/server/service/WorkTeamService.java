package br.com.muttley.security.server.service;

import br.com.muttley.domain.service.Service;
import br.com.muttley.model.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.WorkTeam;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface WorkTeamService extends Service<WorkTeam, ObjectId> {
    WorkTeam findByName(final Owner owner, final String name);

    List<WorkTeam> findByUserMaster(final Owner owner, final User user);
}
