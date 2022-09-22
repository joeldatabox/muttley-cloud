package br.com.muttley.security.server.service.impl;

import br.com.muttley.domain.service.Validator;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.headers.services.MetadataService;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.Passaport;
import br.com.muttley.security.server.events.NoSecurityOwnerCreateEvent;
import br.com.muttley.security.server.events.OwnerCreateEvent;
import br.com.muttley.security.server.repository.OwnerRepository;
import br.com.muttley.security.server.service.NoSecurityOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira on 21/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class NoSecurityOwnerServiceImpl implements NoSecurityOwnerService {
    protected final OwnerRepository repository;
    @Autowired
    protected MetadataService metadataService;
    private final MongoTemplate template;

    @Autowired
    protected Validator validator;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public NoSecurityOwnerServiceImpl(final MongoTemplate template, final OwnerRepository repository, final ApplicationEventPublisher publisher) {
        this.template = template;
        this.repository = repository;
        this.publisher = publisher;
    }

    @Override
    public Owner findByName(final String name) {
        //validando contexto de execução
        //this.validateContext();
        final Owner owner = this.repository.findByName(name);
        if (owner == null) {
            throw new MuttleyNotFoundException(Owner.class, "name", "Registro não encontrado");
        }
        return owner;
    }

    @Override
    public Owner save(final Owner owner) {
        //validando contexto de execução
        //this.validateContext();
        //verificando se realmente está criando um novo registro
        if (owner.getId() != null) {
            throw new MuttleyBadRequestException(Passaport.class, "id", "Não é possível criar um registro com um id existente");
        }
        //validando dados
        this.validator.validate(owner);
        /*//verificando precondições
        this.checkPrecondictionSave(user, value);
        this.beforeSave(user, value);*/
        //garantindo que o metadata ta preenchido
        this.metadataService.generateNewMetadataFor(owner.getUserMaster(), owner);
        final Owner salvedOwner = this.repository.save(owner);
        this.publisher.publishEvent(new NoSecurityOwnerCreateEvent(salvedOwner));
        return salvedOwner;
    }
}
