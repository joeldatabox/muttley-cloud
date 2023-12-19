package br.com.muttley.security.infra.service.impl;

import br.com.muttley.localcache.services.LocalXAPITokenService;
import br.com.muttley.localcache.services.impl.AbstractLocalXAPITokenServiceImpl;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.XAPIToken;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.XAPITokenClient;
import br.com.muttley.security.feign.auth.AuthenticationTokenServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocalXAPITokenServiceImpl extends AbstractLocalXAPITokenServiceImpl implements LocalXAPITokenService {
    private final XAPITokenClient XAPITokenClient;
    private final AuthenticationTokenServiceClient authenticationTokenServiceClient;

    @Autowired
    public LocalXAPITokenServiceImpl(RedisService redisService, XAPITokenClient XAPITokenClient, final AuthenticationTokenServiceClient authenticationTokenServiceClient) {
        super(redisService);
        this.XAPITokenClient = XAPITokenClient;
        this.authenticationTokenServiceClient = authenticationTokenServiceClient;
    }

    @Override
    public XAPIToken loadAPIToken(String token) {
        final XAPIToken XAPIToken;
        if (this.redisService.hasKey(getBasicKey(token, Type.XAPIToken))) {
            XAPIToken = super.loadAPIToken(token);
        } else {
            XAPIToken = this.XAPITokenClient.getByToken(token);
            this.saveInCache(XAPIToken);
        }
        return XAPIToken;
    }

    @Override
    public JwtToken loadJwtTokenFrom(String xAPIToken) {
        final JwtToken jwtToken;
        if (this.redisService.hasKey(getBasicKey(xAPIToken, Type.JWTToken))) {
            jwtToken = super.loadJwtTokenFrom(xAPIToken);
        } else {
            jwtToken = this.authenticationTokenServiceClient.getUserFromXAPIToken(xAPIToken);
            this.saveInCache(xAPIToken, jwtToken);
        }
        return jwtToken;
    }
}
