package br.com.muttley.security.infra.service.impl;

import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.OwnerDataImpl;
import br.com.muttley.model.security.UserDataImpl;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.OwnerServiceClient;
import br.com.muttley.security.infra.service.LocalOwnerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira 25/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class LocalOwnerServiceImpl implements LocalOwnerService {
    private final RedisService redisService;
    private final OwnerServiceClient ownerServiceClient;
    private static final long timeout = 1000 * 60 * 60 * 24;//timeout de 24hr

    @Autowired
    public LocalOwnerServiceImpl(final RedisService redisService, final OwnerServiceClient ownerServiceClient) {
        this.redisService = redisService;
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

    private void saveOwnerInCache(final OwnerData owner) {
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

    private OwnerData loadOwerInCache(final String id) {
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

    private String getBasicKey(final OwnerData ownerData) {
        return getBasicKey(ownerData.getId());
    }

    private String getBasicKey(final String id) {
        return LocalOwnerService.BASIC_KEY + id;
    }
}
