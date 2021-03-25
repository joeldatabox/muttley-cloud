package br.com.muttley.security.server.service.impl;

import br.com.muttley.localcache.services.impl.AbstractLocalDatabindingServiceImpl;
import br.com.muttley.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira 25/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class LocalDatabindingServiceImpl extends AbstractLocalDatabindingServiceImpl {
    @Autowired
    public LocalDatabindingServiceImpl(final RedisService redisService) {
        super(redisService);
    }
}
