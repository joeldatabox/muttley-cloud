package br.com.muttley.security.server.repository;

import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.Passaport;
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
public interface PassaportRepository extends DocumentMongoRepository<Passaport> {
    @Query("{'owner': {'$ref' : ?#{@documentNameConfig.getNameCollectionOwner()}, '$id' : ?#{[0].getId()}}, 'name': '?1' }")
    Passaport findByName(final Owner owner, final String name);

    @Query("{'owner': {'$ref' : ?#{@documentNameConfig.getNameCollectionOwner()}, '$id' : ?#{[0].getId()}}, 'userMaster': {'$ref' : ?#{@documentNameConfig.getNameCollectionUser()}, '$id' : ?#{[1].getId()}}}")
    List<Passaport> findByUserMaster(final Owner owner, final User user);

    @Query("{'owner': {'$ref' : ?#{@documentNameConfig.getNameCollectionOwner()}, '$id' : ?#{[0].getId()}}}")
    List<Passaport> findAll(final Owner owner);

    @Query(value = "{'owner': {'$ref' : ?#{@documentNameConfig.getNameCollectionOwner()}, '$id' : ?#{[0].getId()}}}", count = true)
    Long count(final Owner owner);
}
