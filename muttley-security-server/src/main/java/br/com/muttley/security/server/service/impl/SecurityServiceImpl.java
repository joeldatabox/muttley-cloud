package br.com.muttley.security.server.service.impl;

import br.com.muttley.domain.service.impl.ServiceImpl;
import br.com.muttley.model.Document;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import br.com.muttley.security.server.service.SecurityService;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Joel Rodrigues Moreira on 22/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class SecurityServiceImpl<T extends Document> extends ServiceImpl<T> implements SecurityService<T> {
    public SecurityServiceImpl(final DocumentMongoRepository<T> repository, final MongoTemplate mongoTemplate, final Class<T> clazz) {
        super(repository, mongoTemplate, clazz);
    }
}
