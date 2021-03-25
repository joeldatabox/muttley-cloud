package br.com.muttley.security.infra.service.impl;

import br.com.muttley.localcache.services.LocalOwnerService;
import br.com.muttley.localcache.services.impl.AbstractLocalOwnerServiceImpl;
import br.com.muttley.model.security.OwnerData;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.OwnerServiceClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira 25/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class LocalOwnerServiceImpl extends AbstractLocalOwnerServiceImpl implements LocalOwnerService {
    private final OwnerServiceClient ownerServiceClient;

    @Autowired
    public LocalOwnerServiceImpl(final RedisService redisService, final OwnerServiceClient ownerServiceClient) {
        super(redisService);
        this.ownerServiceClient = ownerServiceClient;
    }

    @Override
    public OwnerData loadOwnerAny() {
        final List<OwnerData> owners = this.ownerServiceClient.findByUser();
        final OwnerData firt = owners.get(0);
        //salvando esse owner no cache
        this.saveOwnerInCache(firt);
        return firt;
    }

    @Override
    public OwnerData loadOwnerById(final String id) {
        final OwnerData owner;
        //verificando se existe esse registro em cache
        if (this.redisService.hasKey(this.getBasicKey(id))) {
            owner = this.loadOwerInCache(id);
        } else {
            //recuperando o owner do servidor
            owner = this.ownerServiceClient.findByUserAndId(id);
            //salvando o owner recuperado no cache
            this.saveOwnerInCache(owner);
        }
        return owner;
    }


}
