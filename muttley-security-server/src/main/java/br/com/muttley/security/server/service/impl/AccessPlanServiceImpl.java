package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyConflictException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.AccessPlan;
import br.com.muttley.model.security.User;
import br.com.muttley.security.server.repository.AccessPlanRepository;
import br.com.muttley.security.server.service.AccessPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class AccessPlanServiceImpl extends SecurityServiceImpl<AccessPlan> implements AccessPlanService {
    final AccessPlanRepository repository;

    @Autowired
    public AccessPlanServiceImpl(final AccessPlanRepository repository) {
        super(repository, AccessPlan.class);
        this.repository = repository;
    }

    @Override
    public AccessPlan findByDescription(final String descricao) {
        final AccessPlan AccessPlan = this.repository.findByName(descricao);
        if (AccessPlan == null) {
            throw new MuttleyNotFoundException(AccessPlan.class, "descricao", "Registro não encontrado");
        }
        return AccessPlan;
    }

    @Override
    public void checkPrecondictionSave(final User user, final AccessPlan value) {
        //verificando se já existe um AccessPlan com a descrição informada
        try {
            findByDescription(value.getDescription());
            throw new MuttleyConflictException(clazz, "descricao", "Já existe um registro com essa descrição");
        } catch (MuttleyNotFoundException ex) {
        }
        super.checkPrecondictionSave(user, value);
    }

    @Override
    public void checkPrecondictionUpdate(final User user, final AccessPlan value) {
        //verificando se a descricao alterada já existe
        try {
            if (!findByDescription(value.getDescription()).equals(value)) {
                throw new MuttleyConflictException(clazz, "descricao", "Já existe um registro com essa descrição");
            }
        } catch (MuttleyNotFoundException ex) {
        }
        super.checkPrecondictionUpdate(user, value);
    }
}
