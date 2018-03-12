package br.com.muttley.security.infra.repository;

import br.com.muttley.model.security.model.preference.UserPreferences;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
@Repository
public interface UserPreferencesRepository extends MongoRepository<UserPreferences, ObjectId> {
    @Query("{'user': {'$ref' : 'users', '$id' : ?0} }")
    UserPreferences findByUser(final String idUser);
}

