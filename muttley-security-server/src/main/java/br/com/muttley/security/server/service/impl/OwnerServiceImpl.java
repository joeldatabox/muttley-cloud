package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.Owner;
import br.com.muttley.security.server.repository.OwnerRepository;
import br.com.muttley.security.server.service.OwnerService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Service do owner do odin
 */
@Service
public class OwnerServiceImpl extends SecurityServiceImpl<Owner, ObjectId> implements OwnerService {
    private final OwnerRepository repository;

    @Autowired
    public OwnerServiceImpl(final OwnerRepository repository) {
        super(repository, Owner.class);
        this.repository = repository;
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
