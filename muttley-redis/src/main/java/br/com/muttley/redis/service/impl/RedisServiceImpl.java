package br.com.muttley.redis.service.impl;

import br.com.muttley.redis.service.RedisService;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Joel Rodrigues Moreira on 08/01/18.
 * @project demo
 */
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOps;
    private final String basicKey;


    public RedisServiceImpl(final String basicKey, final RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        this.basicKey = basicKey;
    }

    @PostConstruct
    private void init() {
        this.hashOps = redisTemplate.opsForHash();
    }

    @Override
    public String getBasicKey() {
        return this.basicKey;
    }

    @Override
    public void set(final Object value) {
        this.set(String.valueOf(value.hashCode()), value);
    }

    @Override
    public void set(final Object value, final long time) {
        this.set(String.valueOf(value.hashCode()), value, time);
    }

    @Override
    public void set(final String key, final Object value) {
        this.hashOps.put(this.getBasicKey(), key, value);
    }

    @Override
    public void set(final String key, final Object value, final long time) {
        this.hashOps.put(this.getBasicKey(), key, value);
        this.redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
    }

    @Override
    public Object get(final String key) {
        return this.hashOps.get(this.getBasicKey(), key);
    }

    @Override
    public void delete(final String key) {
        this.hashOps.delete(getBasicKey(), key);
    }

    @Override
    public Collection<Object> list() {
        return (List<Object>) this.hashOps.multiGet(getBasicKey(), this.hashOps.keys(getBasicKey()));
    }

    @Override
    public void clearAll() {
        this.hashOps.delete(getBasicKey(), this.hashOps.keys(this.getBasicKey()));
    }
}
