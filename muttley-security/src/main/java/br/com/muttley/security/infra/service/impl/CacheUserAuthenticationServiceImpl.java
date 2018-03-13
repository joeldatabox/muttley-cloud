package br.com.muttley.security.infra.service.impl;

import br.com.muttley.model.security.jwt.JwtUser;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.infra.service.CacheUserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 09/01/18.
 * @project demo
 */
public class CacheUserAuthenticationServiceImpl implements CacheUserAuthenticationService {

    private final RedisService redisService;
    private final int expiration;

    @Autowired
    public CacheUserAuthenticationServiceImpl(final RedisService redisService, final @Value("${muttley.security.jwt.token.expiration}") int expiration) {
        this.redisService = redisService;
        this.expiration = expiration;
    }

    @Override
    public void set(final String token, final JwtUser user) {
        final Map<String, Object> mapJwtUser = new HashMap<>(7);
        mapJwtUser.put("id", user.getId());
        mapJwtUser.put("email", user.getEmail());
        mapJwtUser.put("password", user.getPassword());
        mapJwtUser.put("authorities", user.getAuthorities());
        mapJwtUser.put("lastPasswordResetDate", user.getLastPasswordResetDate());
        mapJwtUser.put("originUser", user.getOriginUser());
        mapJwtUser.put("username", user.getUsername());
        this.redisService.set(token, mapJwtUser, expiration);
    }

    @Override
    public JwtUser get(final String token) {
        final Map<String, Object> mapJwtUser = (Map<String, Object>) redisService.get(token);
        if (mapJwtUser != null) {
            return new JwtUser.UserBuilder()
                    .setId((String) mapJwtUser.get("id"))
                    .setEmail((String) mapJwtUser.get("email"))
                    .setPassword((String) mapJwtUser.get("password"))
                    .setAuthorities((Collection<? extends GrantedAuthority>) mapJwtUser.get("authorities"))
                    .setLastPasswordResetDate(mapJwtUser.get("lastPasswordResetDate") != null ? new Date(Long.valueOf(mapJwtUser.get("lastPasswordResetDate").toString())) : null)
                    //.setOriginUser((User) mapJwtUser.get("originUser"))
                    .setName((String) mapJwtUser.get("username"))
                    .build();
        }
        return null;
    }

    @Override
    public boolean contains(final String token) {
        return redisService.hasKey(token);
    }
}
