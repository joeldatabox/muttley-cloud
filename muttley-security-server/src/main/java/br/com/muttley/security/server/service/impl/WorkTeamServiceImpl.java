package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.security.server.repository.WorkTeamRepository;
import br.com.muttley.security.server.service.WorkTeamService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Service do owner do odin
 */
@Service
public class WorkTeamServiceImpl extends SecurityServiceImpl<WorkTeam, ObjectId> implements WorkTeamService {
    private final WorkTeamRepository repository;

    @Autowired
    public WorkTeamServiceImpl(final WorkTeamRepository repository) {
        super(repository, WorkTeam.class);
        this.repository = repository;
    }

    @Override
    public WorkTeam findByName(final Owner owner, final String name) {
        final WorkTeam cwt = repository.findByName(owner.getId().toString(), name);
        if (isNull(cwt)) {
            throw new MuttleyNotFoundException(WorkTeam.class, "name", "Registro não encontrado")
                    .addDetails("name", name);
        }
        return cwt;
    }

    @Override
    public List<WorkTeam> findByUserMaster(final Owner owner, final User user) {
        final List<WorkTeam> itens = repository.findByUserMaster(owner.getId().toString(), user.getId());
        if (isEmpty(itens)) {
            throw new MuttleyNoContentException(WorkTeam.class, "name", "Nenhum time de trabalho encontrado");
        }
        return itens;
    }
}