package br.com.muttley.localcache.services.impl;

import br.com.muttley.localcache.services.LocalRSAKeyPairService;
import br.com.muttley.redis.service.RedisService;

public abstract class AbstractLocalRSAKeyPairService implements LocalRSAKeyPairService {
    private final RedisService service;

    protected AbstractLocalRSAKeyPairService(RedisService service) {
        this.service = service;
    }

    protected String getBasicKeyPublic() {
        return LocalRSAKeyPairService.BASIC_KEY_PUBLIC;
    }

    protected String getBasicKeyPrivate() {
        return LocalRSAKeyPairService.BASIC_KEY_PUBLIC;
    }
}
