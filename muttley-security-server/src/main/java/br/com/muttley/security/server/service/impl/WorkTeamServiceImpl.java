package br.com.muttley.security.server.service.impl;

import br.com.muttley.model.security.User;
import br.com.muttley.model.workteam.WorkTeam;
import br.com.muttley.security.server.repository.WorkTeamRepository;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira on 03/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class WorkTeamServiceImpl extends SecurityServiceImpl<WorkTeam> implements WorkTeamService {
    private final WorkTeamRepository repository;

    @Autowired
    public WorkTeamServiceImpl(WorkTeamRepository repository, MongoTemplate mongoTemplate) {
        super(repository, mongoTemplate, WorkTeam.class);
        this.repository = repository;
    }

    @Override
    public void beforeSave(User user, WorkTeam workTeam) {
        //garantindo informações cruciais
        workTeam.setOwner(user);
        super.beforeSave(user, workTeam);
    }

    @Override
    public void checkPrecondictionSave(User user, WorkTeam value) {
        super.checkPrecondictionSave(user, value);
    }

    @Override
    public void beforeUpdate(User user, WorkTeam workTeam) {
        //garantindo que não será alterado informações cruciais
        workTeam.setOwner(user.getCurrentOwner());
        super.beforeUpdate(user, workTeam);
    }
}
