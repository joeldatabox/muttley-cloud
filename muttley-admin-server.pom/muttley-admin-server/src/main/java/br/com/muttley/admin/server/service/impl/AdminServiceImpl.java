package br.com.muttley.admin.server.service.impl;

import br.com.muttley.domain.service.impl.ServiceImpl;
import br.com.muttley.model.Document;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import br.com.muttley.admin.server.service.AdminService;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Joel Rodrigues Moreira 20/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AdminServiceImpl<T extends Document> extends ServiceImpl<T> implements AdminService<T> {
    public AdminServiceImpl(final DocumentMongoRepository<T> repository, final MongoTemplate mongoTemplate, final Class<T> clazz) {
        super(repository, mongoTemplate, clazz);
    }
}
