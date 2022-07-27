package br.com.muttley.security.server.service.impl;

import br.com.muttley.localcache.services.impl.AbstractLocalWorkTemaServiceImpl;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.workteam.WorkTeamDomain;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.muttley.localcache.services.LocalWorkTeamService.getBasicKey;

/**
 * @author Joel Rodrigues Moreira on 21/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class LocalWorkTeamServiceImpl extends AbstractLocalWorkTemaServiceImpl {
    private final WorkTeamService service;

    @Autowired
    protected LocalWorkTeamServiceImpl(RedisService redisService, WorkTeamService service) {
        super(redisService);
        this.service = service;
    }

    @Override
    public WorkTeamDomain getWorkTeamDomain(JwtToken token, User user) {
        final WorkTeamDomain workTeamDomain;
        if (this.redisService.hasKey(getBasicKey(user.getCurrentOwner(), user))) {
            workTeamDomain = (WorkTeamDomain) redisService.get(getBasicKey(user.getCurrentOwner(), user));
        } else {
            workTeamDomain = service.loadDomain(user);
            this.save(token, user, workTeamDomain);
        }
        return workTeamDomain;
    }
}
