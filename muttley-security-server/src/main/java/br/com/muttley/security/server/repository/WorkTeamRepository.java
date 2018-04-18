package br.com.muttley.security.server.repository;

import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 01/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Repository
public interface WorkTeamRepository extends DocumentMongoRepository<WorkTeam, ObjectId> {
    @Query("{'owner': {'$ref' : 'client-owners', '$id' : ?0}, 'name': '?1' }")
    WorkTeam findByName(final String ownerId, final String name);

    @Query("{'owner': {'$ref' : 'client-owners', '$id' : ?0}, 'userMaster': {'$ref' : 'users', '$id' : '?1'} }")
    List<WorkTeam> findByUserMaster(final String ownerId, final String userId);
}
