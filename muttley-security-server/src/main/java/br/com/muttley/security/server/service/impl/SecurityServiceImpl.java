package br.com.muttley.security.server.service.impl;

import br.com.muttley.domain.impl.ServiceImpl;
import br.com.muttley.model.Document;
import br.com.muttley.mongo.repository.SimpleTenancyMongoRepository;
import br.com.muttley.security.server.service.SecurityService;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Joel Rodrigues Moreira on 22/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class SecurityServiceImpl<T extends Document> extends ServiceImpl<T> implements SecurityService<T> {
    public SecurityServiceImpl(final SimpleTenancyMongoRepository<T> repository, final MongoTemplate mongoTemplate, final Class<T> clazz) {
        super(repository, mongoTemplate, clazz);
    }
}
