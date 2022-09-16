package br.com.muttley.security.infra.service.impl;

import br.com.muttley.localcache.services.LocalXAPITokenService;
import br.com.muttley.localcache.services.impl.AbstractLocalXAPITokenServiceImpl;
import br.com.muttley.model.security.XAPIToken;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.XAPITokenClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocalXAPITokenServiceImpl extends AbstractLocalXAPITokenServiceImpl implements LocalXAPITokenService {
    private final XAPITokenClient XAPITokenClient;

    @Autowired
    public LocalXAPITokenServiceImpl(RedisService redisService, XAPITokenClient XAPITokenClient) {
        super(redisService);
        this.XAPITokenClient = XAPITokenClient;
    }

    @Override
    public XAPIToken loadAPIToken(String token) {
        final XAPIToken XAPIToken;
        if (this.redisService.hasKey(getBasicKey(token))) {
            XAPIToken = super.loadAPIToken(token);
        } else {
            XAPIToken = this.XAPITokenClient.getByToken(token);
            this.saveInCache(XAPIToken);
        }
        return XAPIToken;
    }
}
