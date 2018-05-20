package br.com.muttley.security.server.repository;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
@Repository
public interface UserPreferencesRepository extends DocumentMongoRepository<UserPreferences, ObjectId> {
    UserPreferences findByUser(final User idUser);
}

