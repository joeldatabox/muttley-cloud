package br.com.muttley.admin.server.service.impl;

import br.com.muttley.admin.server.repository.AdminWorkTeamRepository;
import br.com.muttley.admin.server.service.NoSecurityAdminWorkTeamService;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.model.admin.AdminWorkTeam;
import br.com.muttley.model.security.WorkTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class NoSecurityAdminWorkTeamServiceImpl extends AbstractNoSecurityService implements NoSecurityAdminWorkTeamService {
    private final AdminWorkTeamRepository repository;

    @Autowired
    public NoSecurityAdminWorkTeamServiceImpl(final AdminWorkTeamRepository repository) {
        this.repository = repository;
    }

    @Override
    public AdminWorkTeam save(final AdminWorkTeam workTeam) {
        //validando contexto de execução
        this.validateContext();
        //verificando se realmente está criando um novo registro
        if (workTeam.getId() != null) {
            throw new MuttleyBadRequestException(WorkTeam.class, "id", "Não é possível criar um registro com um id existente");
        }
        //validando dados
        this.validator.validate(workTeam);
        /*//verificando precondições
        this.checkPrecondictionSave(user, value);
        this.beforeSave(user, value);*/
        return this.repository.save(workTeam);
    }
}
