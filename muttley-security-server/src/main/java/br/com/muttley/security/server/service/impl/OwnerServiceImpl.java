package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.events.OwnerCreateEvent;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.security.server.repository.OwnerRepository;
import br.com.muttley.security.server.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Service do owner do odin
 */
@Service
public class OwnerServiceImpl extends SecurityServiceImpl<Owner> implements OwnerService {
    private final OwnerRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public OwnerServiceImpl(final OwnerRepository repository, final ApplicationEventPublisher eventPublisher) {
        super(repository, Owner.class);
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Owner save(final User user, final Owner value) {
        if (value.getUserMaster() == null || value.getUserMaster().getId() == null) {
            throw new MuttleyBadRequestException(Owner.class, "userMaster", "Informe um usuário válido");
        }
        if (value.getAccessPlan() == null || value.getAccessPlan().getId() == null) {
            throw new MuttleyBadRequestException(Owner.class, "accessPlan", "Informe um plano de acesso");
        }
        final Owner salvedOwner = super.save(user, value);
        this.eventPublisher.publishEvent(new OwnerCreateEvent(salvedOwner));
        return salvedOwner;
    }

    @Override
    public Owner update(final User user, final Owner value) {
        if (value.getUserMaster() == null || value.getUserMaster().getId() == null) {
            throw new MuttleyBadRequestException(Owner.class, "userMaster", "Informe um usuário válido");
        }
        return super.update(user, value);
    }

    @Override
    public Owner findByName(final String name) {
        final Owner clienteOwner = repository.findByName(name);
        if (isNull(clienteOwner))
            throw new MuttleyNotFoundException(Owner.class, "name", "Registro não encontrado")
                    .addDetails("name", name);
        return clienteOwner;
    }
}
