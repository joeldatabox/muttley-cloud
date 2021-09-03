package br.com.muttley.domain.service.impl;

import br.com.muttley.localcache.services.impl.AbstractLocalModelServiceImpl;
import br.com.muttley.model.Model;
import br.com.muttley.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira on 02/09/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class LocalModelServiceImpl<T extends Model> extends AbstractLocalModelServiceImpl<T> {

    @Autowired
    public LocalModelServiceImpl(final RedisService redisService) {
        super(redisService);
    }
}
