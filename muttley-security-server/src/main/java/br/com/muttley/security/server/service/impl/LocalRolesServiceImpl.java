package br.com.muttley.security.server.service.impl;

import br.com.muttley.localcache.services.impl.AbstractLocalRolesServiceImpl;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.server.events.CurrentRolesResolverEvent;
import br.com.muttley.security.server.events.CurrentRolesResolverEvent.CurrentRolesResolverEventItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira 25/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class LocalRolesServiceImpl extends AbstractLocalRolesServiceImpl {
    private final ApplicationEventPublisher publisher;

    @Autowired
    public LocalRolesServiceImpl(final RedisService redisService, final ApplicationEventPublisher publisher) {
        super(redisService);
        this.publisher = publisher;
    }

    @Override
    public Set<Role> loadCurrentRoles(final JwtToken token, final User user) {
        final Set<Role> roles;
        //verificando se já existe roles para esse usuário
        if (this.redisService.hasKey(this.getBasicKey(user))) {
            roles = this.loadRolesInCache(user);
        } else {
            final CurrentRolesResolverEvent event = new CurrentRolesResolverEvent(new CurrentRolesResolverEventItem(token, user));
            publisher.publishEvent(event);
            //se chegou até aqui precisaremos buscar as roles do server
            roles = event.getRoles();
            //salvando as roles no cache
            this.saveRolesInCache(token, user, roles);
        }
        return roles;
    }
}
