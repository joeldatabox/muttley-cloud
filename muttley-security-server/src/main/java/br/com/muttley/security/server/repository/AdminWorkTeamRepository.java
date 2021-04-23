package br.com.muttley.security.server.repository;

import br.com.muttley.model.admin.AdminWorkTeam;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 01/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Repository
public interface AdminWorkTeamRepository extends DocumentMongoRepository<AdminWorkTeam> {
    @Query("{'owner': {'$ref' : ?#{@documentNameConfig.getNameCollectionAdminOwner()}, '$id' : ?#{[0].getId()}}, 'name': '?1' }")
    AdminWorkTeam findByName(final Owner owner, final String name);

    @Query("{'owner': {'$ref' : ?#{@documentNameConfig.getNameCollectionAdminOwner()}, '$id' : ?#{[0].getId()}}, 'userMaster': {'$ref' : ?#{@documentNameConfig.getNameCollectionUser()}, '$id' : ?#{[1].getId()}}}")
    List<AdminWorkTeam> findByUserMaster(final Owner owner, final User user);

    @Query("{'owner': {'$ref' : ?#{@documentNameConfig.getNameCollectionAdminOwner()}, '$id' : ?#{[0].getId()}}}")
    List<AdminWorkTeam> findAll(final Owner owner);

    @Query(value = "{'owner': {'$ref' : ?#{@documentNameConfig.getNameCollectionAdminOwner()}, '$id' : ?#{[0].getId()}}}", count = true)
    Long count(final Owner owner);
}
