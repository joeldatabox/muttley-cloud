package br.com.muttley.security.infra.repository;

import br.com.muttley.model.security.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}

