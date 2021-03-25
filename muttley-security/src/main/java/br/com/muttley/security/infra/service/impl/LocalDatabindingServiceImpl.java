package br.com.muttley.security.infra.service.impl;

import br.com.muttley.localcache.services.LocalDatabindingService;
import br.com.muttley.localcache.services.impl.AbstractLocalDatabindingServiceImpl;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserDataBinding;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.UserDataBindingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira 25/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class LocalDatabindingServiceImpl extends AbstractLocalDatabindingServiceImpl implements LocalDatabindingService {
    private final UserDataBindingClient dataBindingService;

    @Autowired
    public LocalDatabindingServiceImpl(final RedisService redisService, final UserDataBindingClient dataBindingService) {
        super(redisService);
        this.dataBindingService = dataBindingService;
    }

    @Override
    public List<UserDataBinding> getUserDataBindings(final JwtToken jwtUser, final User user) {
        final List<UserDataBinding> dataBindings;
        //verificando se já existe no cache
        if (this.redisService.hasKey(this.getBasicKey(user))) {
            //recuperando dos itens
            dataBindings = (List<UserDataBinding>) this.redisService.get(this.getBasicKey(user));
        } else {
            dataBindings = this.dataBindingService.list();
            //salvando no cache
            this.redisService.set(this.getBasicKey(user), dataBindings, jwtUser.getExpiration());
        }
        return dataBindings;
    }
}
