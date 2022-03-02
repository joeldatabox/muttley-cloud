package br.com.muttley.security.server.repository;

import br.com.muttley.model.WorkTeam;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 02/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface WorkTeamRepository extends DocumentMongoRepository<WorkTeam> {
    @Query("{'owner': {'$ref' : ?#{@documentNameConfig.getNameCollectionOwner()}, '$id' : ?#{[0].getId()}}, 'name': '?1' }")
    WorkTeam findByName(final Owner owner, final String name);

    @Query("{'owner': {'$ref' : ?#{@documentNameConfig.getNameCollectionOwner()}, '$id' : ?#{[0].getId()}}, 'userMaster': {'$ref' : ?#{@documentNameConfig.getNameCollectionUser()}, '$id' : ?#{[1].getId()}}}")
    List<WorkTeam> findByUserMaster(final Owner owner, final User user);

    @Query("{'owner': {'$ref' : ?#{@documentNameConfig.getNameCollectionOwner()}, '$id' : ?#{[0].getId()}}}")
    List<WorkTeam> findAll(final Owner owner);

    @Query(value = "{'owner': {'$ref' : ?#{@documentNameConfig.getNameCollectionOwner()}, '$id' : ?#{[0].getId()}}}", count = true)
    Long count(final Owner owner);
}
