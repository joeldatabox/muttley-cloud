package br.com.muttley.admin.server.repository;

import br.com.muttley.model.admin.AdminOwner;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Joel Rodrigues Moreira 20/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Repository
public interface AdminOwnerRepository extends DocumentMongoRepository<AdminOwner> {
    public AdminOwner findByName(final String name);
}
