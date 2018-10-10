package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.events.WorkTeamCreateEvent;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.security.server.repository.WorkTeamRepository;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static java.util.Objects.isNull;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Service do owner do odin
 */
@Service
public class WorkTeamServiceImpl extends SecurityServiceImpl<WorkTeam> implements WorkTeamService {
    private final WorkTeamRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public WorkTeamServiceImpl(final WorkTeamRepository repository, final ApplicationEventPublisher eventPublisher) {
        super(repository, WorkTeam.class);
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void afterSave(final User user, final WorkTeam value) {
        this.eventPublisher.publishEvent(new WorkTeamCreateEvent(value));
        super.afterSave(user, value);
    }

    @Override
    public WorkTeam findByName(final Owner owner, final String name) {
        final WorkTeam cwt = repository.findByName(owner, name);
        if (isNull(cwt)) {
            throw new MuttleyNotFoundException(WorkTeam.class, "name", "Registro não encontrado")
                    .addDetails("name", name);
        }
        return cwt;
    }

    @Override
    public List<WorkTeam> findByUserMaster(final Owner owner, final User user) {
        final List<WorkTeam> itens = repository.findByUserMaster(owner, user);
        if (CollectionUtils.isEmpty(itens)) {
            throw new MuttleyNoContentException(WorkTeam.class, "name", "Nenhum time de trabalho encontrado");
        }
        return itens;
    }
}
