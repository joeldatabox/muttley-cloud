package br.com.muttley.security.infra.service.impl;

import br.com.muttley.localcache.services.LocalRolesService;
import br.com.muttley.localcache.services.impl.AbstractLocalRolesServiceImpl;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.PassaportServiceClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira 25/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class LocalRolesServiceImpl extends AbstractLocalRolesServiceImpl implements LocalRolesService {
    private final PassaportServiceClient passaportService;

    @Autowired
    public LocalRolesServiceImpl(final RedisService redisService, final PassaportServiceClient passaportService) {
        super(redisService);
        this.passaportService = passaportService;
    }

    @Override
    public Set<Role> loadCurrentRoles(final JwtToken token, final User user) {
        final Set<Role> roles;
        //verificando se já existe roles para esse usuário
        if (this.redisService.hasKey(this.getBasicKey(user))) {
            roles = this.loadRolesInCache(user);
        } else {
            //se chegou até aqui precisaremos buscar as roles do server
            roles = this.passaportService.loadCurrentRoles();
            //salvando as roles no cache
            this.saveRolesInCache(token, user, roles);
        }
        return roles;
    }
}
