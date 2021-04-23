package br.com.muttley.admin.server.service.impl;

import br.com.muttley.admin.server.repository.AdminOwnerRepository;
import br.com.muttley.admin.server.service.NoSecurityAdminOwnerService;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.admin.AdminOwner;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.WorkTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class NoSecurityAdminOwnerServiceImpl extends AbstractNoSecurityService implements NoSecurityAdminOwnerService {
    protected final AdminOwnerRepository repository;
    private final MongoTemplate template;

    @Autowired
    public NoSecurityAdminOwnerServiceImpl(final MongoTemplate template, final AdminOwnerRepository repository) {
        this.template = template;
        this.repository = repository;
    }

    @Override
    public AdminOwner findByName(final String name) {
        //validando contexto de execução
        this.validateContext();
        final AdminOwner owner = this.repository.findByName(name);
        if (owner == null) {
            throw new MuttleyNotFoundException(Owner.class, "name", "Registro não encontrado");
        }
        return owner;
    }

    @Override
    public AdminOwner save(final AdminOwner owner) {
        //validando contexto de execução
        this.validateContext();
        //verificando se realmente está criando um novo registro
        if (owner.getId() != null) {
            throw new MuttleyBadRequestException(WorkTeam.class, "id", "Não é possível criar um registro com um id existente");
        }
        //validando dados
        this.validator.validate(owner);
        /*//verificando precondições
        this.checkPrecondictionSave(user, value);
        this.beforeSave(user, value);*/
        return this.repository.save(owner);
    }


}
