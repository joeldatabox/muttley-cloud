package br.com.muttley.security.infra.service.impl;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.WorkTeamServiceClient;
import br.com.muttley.security.infra.service.LocalRolesService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Joel Rodrigues Moreira 25/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class LocalRolesServiceImpl implements LocalRolesService {
    private final RedisService redisService;
    private final WorkTeamServiceClient workTeamService;

    @Autowired
    public LocalRolesServiceImpl(final RedisService redisService, final WorkTeamServiceClient workTeamService) {
        this.redisService = redisService;
        this.workTeamService = workTeamService;
    }

    @Override
    public Set<Role> loadCurrentRoles(final JwtToken token, final User user) {
        final Set<Role> roles;
        //verificando se já existe roles para esse usuário
        if (this.redisService.hasKey(this.getBasicKey(user))) {
            roles = this.loadRolesInCache(user);
        } else {
            //se chegou até aqui precisaremos buscar as roles do server
            roles = this.workTeamService.loadCurrentRoles();
            //salvando as roles no cache
            this.saveRolesInCache(token, user, roles);
        }
        return roles;
    }

    @Override
    public void expireRoles(final User user) {
        this.redisService.delete(this.getBasicKey(user));
    }

    private void saveRolesInCache(final JwtToken token, final User user, final Set<Role> roles) {
        this.redisService.set(this.getBasicKey(user), roles.parallelStream().map(Role::getRoleName).collect(Collectors.toSet()), token.getExpiration());
    }

    private Set<Role> loadRolesInCache(final User user) {
        return ((List<String>) this.redisService.get(this.getBasicKey(user)))
                .parallelStream()
                .map(it -> Role.valueOf(it))
                .collect(Collectors.toSet());
    }

    private String getBasicKey(final User user) {
        return LocalRolesService.BASIC_KEY + user.getId();
    }
}
