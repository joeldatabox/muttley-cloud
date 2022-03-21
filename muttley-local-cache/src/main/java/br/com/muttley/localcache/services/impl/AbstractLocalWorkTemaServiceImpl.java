package br.com.muttley.localcache.services.impl;

import br.com.muttley.localcache.services.LocalWorkTeamService;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.workteam.WorkTeamDomain;
import br.com.muttley.redis.service.RedisService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Joel Rodrigues Moreira on 21/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractLocalWorkTemaServiceImpl implements LocalWorkTeamService {
    protected final RedisService redisService;

    protected AbstractLocalWorkTemaServiceImpl(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public WorkTeamDomain getWorkTeamDomain(JwtToken token, User user) {
        throw new NotImplementedException();
    }

    @Override
    public LocalWorkTeamService expire(User user) {
        //deletando item do cache
        this.redisService.delete(this.getBasicKey(user));
        return this;
    }

    protected void save(final JwtToken token, final User user, final WorkTeamDomain domain) {
        this.redisService.set(this.getBasicKey(user), domain, token.getDtExpiration());
    }

    protected WorkTeamDomain loadWorkTeamDomainInCache(final User user) {
        return (WorkTeamDomain) this.redisService.get(this.getBasicKey(user));
    }

    protected String getBasicKey(final User user) {
        return BASIC_KEY + user.getId();
    }
}
