package br.com.muttley.admin.server.repository;

import br.com.muttley.model.admin.AdminUserDataBinding;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserDataBinding;
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
public interface AdminUserDataBindingRepository extends DocumentMongoRepository<AdminUserDataBinding> {

    @Query("{'owner': {'$ref' : ?#{@documentNameConfig.getNameCollectionOwner()}, '$id' : ?#{[0].getId()}}, 'user': {'$ref' : ?#{@documentNameConfig.getNameCollectionUser()}, '$id' : ?#{[1].getId()}}}")
    List<UserDataBinding> findByUser(final Owner owner, final User user);

    @Query("{'owner': {'$ref' : ?#{@documentNameConfig.getNameCollectionOwner()}, '$id' : ?#{[0].getId()}}, 'user': {'$ref' : ?#{@documentNameConfig.getNameCollectionUser()}, '$id' : ?#{[1].getId()}}, 'key': '?2'}")
    UserDataBinding findByUserAndKey(final Owner owner, final User user, final String key);
}
