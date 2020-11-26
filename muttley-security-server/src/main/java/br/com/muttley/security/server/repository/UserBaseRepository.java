package br.com.muttley.security.server.repository;

import br.com.muttley.model.security.UserBase;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Joel Rodrigues Moreira 26/11/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Repository
public interface UserBaseRepository extends DocumentMongoRepository<UserBase> {
}
