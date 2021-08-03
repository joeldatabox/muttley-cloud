package br.com.muttley.security.server.repository;

import br.com.muttley.model.security.UserBase;
import br.com.muttley.mongo.repository.SimpleTenancyMongoRepository;
import org.springframework.stereotype.Repository;


/**
 * @author Joel Rodrigues Moreira on 22/07/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
//@Repository
public interface UserBaseRepository extends SimpleTenancyMongoRepository<UserBase> {
}

