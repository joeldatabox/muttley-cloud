package br.com.muttley.security.server.repository;

import br.com.muttley.model.admin.AdminOwner;
import br.com.muttley.model.security.User;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Joel Rodrigues Moreira on 22/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Repositório específico para owner de clientes
 */
@Repository
public interface AdminOwnerRepository extends DocumentMongoRepository<AdminOwner> {

    AdminOwner findByName(final String nome);

    boolean existsByUserMaster(final User userMaster);
}