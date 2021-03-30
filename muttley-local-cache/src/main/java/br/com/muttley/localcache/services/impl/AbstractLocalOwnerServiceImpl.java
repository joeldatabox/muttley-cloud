package br.com.muttley.localcache.services.impl;

import br.com.muttley.localcache.services.LocalOwnerService;
import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.OwnerDataImpl;
import br.com.muttley.model.security.UserDataImpl;
import br.com.muttley.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira 25/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractLocalOwnerServiceImpl implements LocalOwnerService {
    protected final RedisService redisService;
    protected static final long timeout = 60 * 60 * 24;//timeout de 24hr

    @Autowired
    public AbstractLocalOwnerServiceImpl(final RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public OwnerData loadOwnerAny() {
        throw new NotImplementedException();
    }

    @Override
    public OwnerData loadOwnerById(final String id) {
        throw new NotImplementedException();
    }

    protected void saveOwnerInCache(final OwnerData owner) {
        final Map<String, Object> ownerMap = new HashMap<>();
        ownerMap.put("id", owner.getId());
        ownerMap.put("name", owner.getName());
        ownerMap.put("description", owner.getDescription());

        if (owner.getUserMaster() != null) {
            final Map<String, Object> userMasterMap = new HashMap<>();
            userMasterMap.put("id", owner.getUserMaster().getId());
            userMasterMap.put("name", owner.getUserMaster().getName());
            userMasterMap.put("description", owner.getUserMaster().getDescription());
            userMasterMap.put("userName", owner.getUserMaster().getUserName());
            userMasterMap.put("nickUsers", owner.getUserMaster().getNickUsers());
            userMasterMap.put("email", owner.getUserMaster().getEmail());
            ownerMap.put("userMaster", userMasterMap);
        }
        this.redisService.set(this.getBasicKey(owner), ownerMap, timeout);
    }

    protected OwnerData loadOwerInCache(final String id) {
        final Map<String, Object> owerMap = (Map<String, Object>) this.redisService.get(this.getBasicKey(id));
        final OwnerDataImpl owner = new OwnerDataImpl();
        owner.setId(String.valueOf(owerMap.get("id")));
        owner.setDescription(String.valueOf(owerMap.get("description")));
        owner.setName(String.valueOf(owerMap.get("name")));
        if (owerMap.containsKey("userMaster")) {
            final UserDataImpl userData = new UserDataImpl();
            final Map<String, Object> userMasterMap = (Map<String, Object>) owerMap.get("userMaster");
            userData.setId(String.valueOf(userMasterMap.get("id")));
            userData.setName(String.valueOf(userMasterMap.get("name")));
            userData.setDescription(String.valueOf(userMasterMap.get("description")));
            userData.setUserName(String.valueOf(userMasterMap.get("userName")));
            userData.setNickUsers((Set<String>) userMasterMap.get("nickUsers"));
            userData.setEmail(String.valueOf(userMasterMap.get("email")));
            owner.setUserMaster(userData);
        }
        return owner;
    }

    protected String getBasicKey(final OwnerData ownerData) {
        return getBasicKey(ownerData.getId());
    }

    protected String getBasicKey(final String id) {
        return LocalOwnerService.BASIC_KEY + id;
    }
}
