package br.com.muttley.localcache.services.impl;

import br.com.muttley.localcache.services.LocalWorkTeamService;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.workteam.WorkTeamDomain;
import br.com.muttley.redis.service.RedisService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static br.com.muttley.localcache.services.LocalWorkTeamService.getBasicKey;
import static br.com.muttley.localcache.services.LocalWorkTeamService.getBasicKeyExpressionOwner;

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
        this.redisService.delete(getBasicKey(user.getCurrentOwner(), user));
        return this;
    }

    @Override
    public LocalWorkTeamService expireByOwner(User user) {
        //deletando todos os itens do cache baseado no owner
        this.redisService.deleteByExpression(getBasicKeyExpressionOwner(user.getCurrentOwner()) + "*");
        return this;
    }

    protected void save(final JwtToken token, final User user, final WorkTeamDomain domain) {
        this.redisService.set(getBasicKey(user.getCurrentOwner(), user), domain, token.getDtExpiration());
    }

    protected WorkTeamDomain loadWorkTeamDomainInCache(final User user) {
        return (WorkTeamDomain) this.redisService.get(getBasicKey(user.getCurrentOwner(), user));
    }

}
