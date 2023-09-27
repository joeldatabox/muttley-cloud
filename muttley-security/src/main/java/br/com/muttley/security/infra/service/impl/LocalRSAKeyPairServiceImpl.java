package br.com.muttley.security.infra.service.impl;

import br.com.muttley.localcache.services.LocalRSAKeyPairService;
import br.com.muttley.localcache.services.impl.AbstractLocalRSAKeyPairService;
import br.com.muttley.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;

public class LocalRSAKeyPairServiceImpl extends AbstractLocalRSAKeyPairService implements LocalRSAKeyPairService {

    @Autowired
    public LocalRSAKeyPairServiceImpl(RedisService service) {
        super(service);
    }
}
