package br.com.muttley.security.server.repository;

import br.com.muttley.model.security.APIToken;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Joel Rodrigues Moreira on 09/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Repository
public interface APITokenRepository extends DocumentMongoRepository<APIToken> {

    APIToken findByToken(final String token);
}
