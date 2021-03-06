package br.com.muttley.security.server.repository;

import br.com.muttley.model.security.User;
import br.com.muttley.mongo.repository.SimpleTenancyMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
@Repository
public interface UserRepository extends SimpleTenancyMongoRepository<User> {
    User findByUserName(final String userName);

    User findByEmail(final String email);
}

