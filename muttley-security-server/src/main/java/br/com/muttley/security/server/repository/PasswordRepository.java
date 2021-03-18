package br.com.muttley.security.server.repository;

import br.com.muttley.model.security.Password;
import br.com.muttley.model.security.User;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Joel Rodrigues Moreira 12/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Repository
public interface PasswordRepository extends DocumentMongoRepository<Password> {
    @Query("{'user': {'$ref' : ?#{@documentNameConfig.getNameCollectionUser()}, '$id' : ?#{[0].getObjectId()}} }")
    Password findByUser(final User user);
}
