package br.com.muttley.localcache.services.impl;

import br.com.muttley.localcache.services.LocalRolesService;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Joel Rodrigues Moreira 25/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractLocalRolesServiceImpl implements LocalRolesService {
    protected final RedisService redisService;

    @Autowired
    public AbstractLocalRolesServiceImpl(final RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public Set<Role> loadCurrentRoles(final JwtToken token, final User user) {
        throw new NotImplementedException();
    }

    @Override
    public void expireRoles(final User user) {
        this.redisService.delete(this.getBasicKey(user));
    }

    protected void saveRolesInCache(final JwtToken token, final User user, final Set<Role> roles) {
        this.redisService.set(this.getBasicKey(user), roles.parallelStream().map(Role::getRoleName).collect(Collectors.toSet()), token.getDtExpiration());
    }

    protected Set<Role> loadRolesInCache(final User user) {
        return ((Set<String>) this.redisService.get(this.getBasicKey(user)))
                .parallelStream()
                .map(it -> Role.valueOf(it))
                .collect(Collectors.toSet());
    }

    protected String getBasicKey(final User user) {
        return LocalRolesService.BASIC_KEY + user.getId();
    }
}
