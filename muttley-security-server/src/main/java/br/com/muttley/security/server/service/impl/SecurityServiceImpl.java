package br.com.muttley.security.server.service.impl;

import br.com.muttley.domain.service.impl.ServiceImpl;
import br.com.muttley.model.Document;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import br.com.muttley.security.server.service.SecurityService;
import org.bson.types.ObjectId;

/**
 * @author Joel Rodrigues Moreira on 22/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project agrifocus-cloud
 */
public class SecurityServiceImpl<T extends Document, ID extends ObjectId> extends ServiceImpl<T, ID> implements SecurityService<T, ID> {
    public SecurityServiceImpl(final DocumentMongoRepository<T, ID> repository, final Class<T> clazz) {
        super(repository, clazz);
    }
}
