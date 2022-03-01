package br.com.muttley.admin.server.service.impl;

import br.com.muttley.admin.server.repository.AdminPassaportRepository;
import br.com.muttley.admin.server.service.NoSecurityAdminPassaportService;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.model.admin.AdminPassaport;
import br.com.muttley.model.security.Passaport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class NoSecurityAdminPassaportServiceImpl extends AbstractNoSecurityService implements NoSecurityAdminPassaportService {
    private final AdminPassaportRepository repository;

    @Autowired
    public NoSecurityAdminPassaportServiceImpl(final AdminPassaportRepository repository) {
        this.repository = repository;
    }

    @Override
    public AdminPassaport save(final AdminPassaport passaport) {
        //validando contexto de execução
        this.validateContext();
        //verificando se realmente está criando um novo registro
        if (passaport.getId() != null) {
            throw new MuttleyBadRequestException(Passaport.class, "id", "Não é possível criar um registro com um id existente");
        }
        //validando dados
        this.validator.validate(passaport);
        /*//verificando precondições
        this.checkPrecondictionSave(user, value);
        this.beforeSave(user, value);*/
        return this.repository.save(passaport);
    }
}
