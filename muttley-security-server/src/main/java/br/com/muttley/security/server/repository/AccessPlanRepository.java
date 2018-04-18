package br.com.muttley.security.server.repository;

import br.com.muttley.model.security.AccessPlan;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

/**
 * @author Joel Rodrigues Moreira on 22/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project agrifocus-cloud
 */
@Repository
public interface AccessPlanRepository extends DocumentMongoRepository<AccessPlan, ObjectId> {
    AccessPlan findByDescription(final String name);
}
