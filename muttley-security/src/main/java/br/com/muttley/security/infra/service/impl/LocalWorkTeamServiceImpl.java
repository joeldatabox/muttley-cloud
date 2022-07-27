package br.com.muttley.security.infra.service.impl;

import br.com.muttley.localcache.services.LocalWorkTeamService;
import br.com.muttley.localcache.services.impl.AbstractLocalWorkTemaServiceImpl;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.workteam.WorkTeamDomain;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.WorkTeamServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.muttley.localcache.services.LocalWorkTeamService.getBasicKey;

/**
 * @author Joel Rodrigues Moreira on 21/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class LocalWorkTeamServiceImpl extends AbstractLocalWorkTemaServiceImpl implements LocalWorkTeamService {
    private final WorkTeamServiceClient client;

    @Autowired
    public LocalWorkTeamServiceImpl(RedisService redisService, WorkTeamServiceClient client) {
        super(redisService);
        this.client = client;
    }

    @Override
    public WorkTeamDomain getWorkTeamDomain(JwtToken token, User user) {
        final WorkTeamDomain workTeamDomain;
        //verificando se existe esse registro em cache
        if (this.redisService.hasKey(getBasicKey(user.getCurrentOwner(), user))) {
            workTeamDomain = this.loadWorkTeamDomainInCache(user);
        } else {
            //recuperando o workteamdomaina do servidor
            workTeamDomain = this.client.loadDomain();
            //salvando o workteamdomaina recuperado no cache
            this.save(token, user, workTeamDomain);
        }
        return workTeamDomain;
    }
}
