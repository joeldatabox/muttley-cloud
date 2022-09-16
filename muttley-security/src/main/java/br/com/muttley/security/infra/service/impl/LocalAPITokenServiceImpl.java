package br.com.muttley.security.infra.service.impl;

import br.com.muttley.localcache.services.LocalAPITokenService;
import br.com.muttley.localcache.services.impl.AbstractLocalAPITokenServiceImpl;
import br.com.muttley.model.security.XAPIToken;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.APITokenClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocalAPITokenServiceImpl extends AbstractLocalAPITokenServiceImpl implements LocalAPITokenService {
    private final APITokenClient apiTokenClient;

    @Autowired
    public LocalAPITokenServiceImpl(RedisService redisService, APITokenClient apiTokenClient) {
        super(redisService);
        this.apiTokenClient = apiTokenClient;
    }

    @Override
    public XAPIToken loadAPIToken(String token) {
        final XAPIToken XAPIToken;
        if (this.redisService.hasKey(getBasicKey(token))) {
            XAPIToken = super.loadAPIToken(token);
        } else {
            XAPIToken = this.apiTokenClient.getByToken(token);
            this.saveInCache(XAPIToken);
        }
        return XAPIToken;
    }
}
