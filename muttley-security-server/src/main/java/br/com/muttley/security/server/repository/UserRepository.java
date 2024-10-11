package br.com.muttley.security.server.repository;

import br.com.muttley.model.security.User;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
@Repository
public interface UserRepository extends DocumentMongoRepository<User> {

    User findByResetToken(String resetToken);

    User findByUserName(final String userName);

    User findByEmail(final String email);

    User findByNickUsers(Set<String> nickUsers);

    User findByEmailSecundario(String email);

}

