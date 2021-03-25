package br.com.muttley.localcache.services.impl;

import br.com.muttley.localcache.services.LocalDatabindingService;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserDataBinding;
import br.com.muttley.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira 25/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractLocalDatabindingServiceImpl implements LocalDatabindingService {

    protected final RedisService redisService;

    @Autowired
    public AbstractLocalDatabindingServiceImpl(final RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public List<UserDataBinding> getUserDataBindings(final JwtToken jwtUser, final User user) {
        throw new NotImplementedException();
    }

    @Override
    public void expireUserDataBindings(final User user) {
        this.redisService.delete(this.getBasicKey(user));
    }

    protected String getBasicKey(final User user) {
        return BASIC_KEY + user.getId();
    }
}
