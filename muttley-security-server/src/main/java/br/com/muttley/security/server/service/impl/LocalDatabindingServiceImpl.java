package br.com.muttley.security.server.service.impl;

import br.com.muttley.localcache.services.impl.AbstractLocalDatabindingServiceImpl;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserDataBinding;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.server.events.CurrentDatabindingResolverEvent;
import br.com.muttley.security.server.events.CurrentDatabindingResolverEvent.CurrentDatabindingEventItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira 25/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class LocalDatabindingServiceImpl extends AbstractLocalDatabindingServiceImpl {
    private final ApplicationEventPublisher publisher;

    @Autowired
    public LocalDatabindingServiceImpl(final RedisService redisService, final ApplicationEventPublisher publisher) {
        super(redisService);
        this.publisher = publisher;
    }

    @Override
    public List<UserDataBinding> getUserDataBindings(final JwtToken jwtUser, final User user) {
        final List<UserDataBinding> dataBindings;
        //verificando se já existe no cache
        if (this.redisService.hasKey(this.getBasicKey(user))) {
            //recuperando dos itens
            dataBindings = this.getDatabinDataBindingsInCache(jwtUser, user);
        } else {
            final CurrentDatabindingResolverEvent event = new CurrentDatabindingResolverEvent(new CurrentDatabindingEventItem(jwtUser, user));
            this.publisher.publishEvent(event);
            dataBindings = event.getDataBindings();
            //salvando no cache
            this.saveDatabindingsInCache(jwtUser, user, dataBindings);
        }
        return dataBindings;
    }
}
