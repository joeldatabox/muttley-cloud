package br.com.muttley.admin.server.service.impl;

import br.com.muttley.admin.server.repository.AdminOwnerRepository;
import br.com.muttley.admin.server.service.AdminOwnerService;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.admin.AdminOwner;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira 20/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class AdminOwnerServiceImpl extends AdminServiceImpl<AdminOwner> implements AdminOwnerService {
    private final AdminOwnerRepository repository;

    @Autowired
    public AdminOwnerServiceImpl(final AdminOwnerRepository repository, final MongoTemplate mongoTemplate) {
        super(repository, mongoTemplate, AdminOwner.class);
        this.repository = repository;
    }

    @Override
    public AdminOwner findByName(final User user, final String name) {
        final AdminOwner owner = this.repository.findByName(name);
        if (owner == null) {
            throw new MuttleyNotFoundException(Owner.class, "name", "Registro não encontrado");
        }
        return owner;
    }

    @Override
    public AdminOwner findById1(final User user, final String id) {
        return super.findById(user, id);
    }
}
