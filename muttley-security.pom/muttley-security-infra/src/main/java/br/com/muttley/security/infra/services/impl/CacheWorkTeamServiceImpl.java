package br.com.muttley.security.infra.services.impl;

import br.com.muttley.model.security.AccessPlan;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.infra.services.CacheWorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 10/11/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Service
public class CacheWorkTeamServiceImpl implements CacheWorkTeamService {
    private final RedisService redisService;
    private static final String KEY = "work-team";

    @Autowired
    public CacheWorkTeamServiceImpl(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public WorkTeam get(User user, String idWorkTeam) {
        return toWorkTeam((Map<String, Object>) redisService.get(createKey(user, idWorkTeam)));
    }

    @Override
    public CacheWorkTeamService set(User user, WorkTeam workTeam, long time) {
        final Map map = toMap(workTeam);
        redisService.set(createKey(user, workTeam.getId()), map, time);
        return this;
    }

    private String createKey(final User user, String idWorkTeam) {
        return KEY + ":" + user.getUserName() + ":" + idWorkTeam;
    }

    private Map<String, Object> toMap(final WorkTeam workTeam) {
        if (workTeam == null) {
            return null;
        }
        final Map<String, Object> map = new HashMap();
        map.put("id", workTeam.getId());
        map.put("name", workTeam.getName());
        map.put("description", workTeam.getDescription());

        if (workTeam.getUserMaster() != null) {
            final Map<String, String> userMaster = new HashMap();
            userMaster.put("id", workTeam.getUserMaster().getId());
            userMaster.put("name", workTeam.getUserMaster().getName());
            userMaster.put("userName", workTeam.getUserMaster().getUserName());
            map.put("userMaster", userMaster);
        }

        if (workTeam.getOwner() != null) {
            final Map<String, String> owner = new HashMap();
            owner.put("id", workTeam.getOwner().getId());
            owner.put("name", workTeam.getOwner().getName());
            owner.put("description", workTeam.getOwner().getDescription());
            owner.put("userMaster", workTeam.getOwner().getUserMaster().getId());
            owner.put("accessPlan", workTeam.getOwner().getAccessPlan().getId());
            map.put("owner", owner);
        }

        if (isEmpty(workTeam.getMembers())) {
            final List<Map> members = new ArrayList<>(workTeam.getMembers().size());
            for (final User user : workTeam.getMembers()) {
                final Map<String, String> userMaster = new HashMap();
                userMaster.put("id", user.getId());
                userMaster.put("userName", user.getUserName());
                members.add(userMaster);
            }
            map.put("members", members);
        }

        return map;
    }

    private WorkTeam toWorkTeam(final Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        final WorkTeam workTeam = new WorkTeam();
        workTeam.setId(map.get("id").toString())
                .setName(map.get("name").toString())
                .setDescription(map.get("description").toString());

        if (map.containsKey("userMaster")) {
            final Map<String, String> mapUserMaster = (Map<String, String>) map.get("userMaster");
            final User userMaster = new User()
                    .setId(mapUserMaster.get("id"))
                    .setName(mapUserMaster.get("name"))
                    .setUserName(mapUserMaster.get("email"));
            workTeam.setUserMaster(userMaster);
        }

        if (map.containsKey("owner")) {
            final Map<String, String> mapOwner = (Map<String, String>) map.get("owner");
            final Owner owner = new Owner()
                    .setId(mapOwner.get("id"))
                    .setName(mapOwner.get("name"))
                    .setDescription(mapOwner.get("description"))
                    .setUserMaster(new User().setId(mapOwner.get("userMaster")))
                    .setAccessPlan(new AccessPlan().setId(mapOwner.get("accessPlan")));
            workTeam.setOwner(owner);
        }

        if (map.containsKey("members")) {
            final List<Map<String, String>> mapMembers = (List<Map<String, String>>) map.get("members");
            mapMembers.forEach(member -> {
                workTeam.addMember(new User().setId(member.get("id")).setUserName(member.get("userName")));
            });
        }
        return workTeam;
    }
}
