package br.com.muttley.redis.service.impl;

import br.com.muttley.redis.service.RedisService;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author Joel Rodrigues Moreira on 08/01/18.
 * @project demo
 */
public class RedisServiceImpl<T> implements RedisService<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOps;
    private final String basicKey;


    public RedisServiceImpl(final String basicKey, final RedisTemplate redisTemplate) {
        this.basicKey = basicKey;
        this.redisTemplate = redisTemplate;
        this.redisTemplate.setValueSerializer(new JsonRedisSerializer());
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
    public RedisService set(final String key, final T value) {
        //redisTemplate.opsForHash().put(getBasicKey(), key, value);
        redisTemplate.opsForValue().set(createKey(key), value);
        return this;
    }

    @Override
    public RedisService set(final String key, final T value, final long time) {
        final String keyValue = createKey(key);
        this.redisTemplate.opsForValue().set(createKey(key), value, time, TimeUnit.MILLISECONDS);
        return this;
    }

    @Override
    public T get(final String key) {
        return (T) this.redisTemplate.opsForValue().get(createKey(key));
    }

    @Override
    public RedisService delete(final String key) {
        this.redisTemplate.delete(createKey(key));
        return this;
    }

    @Override
    public Collection<T> list() {
        return (Collection<T>) this.redisTemplate.opsForValue().multiGet(this.redisTemplate.keys(getBasicKey()));
    }

    @Override
    public RedisService clearAll() {
        this.redisTemplate.delete(this.redisTemplate.keys(getBasicKey()));
        return this;
    }

    @Override
    public boolean hasKey(final String key) {
        return this.redisTemplate.hasKey(createKey(key));
    }

    private String createKey(final String key) {
        return this.getBasicKey() + ":" + key;
    }
}

 class JsonRedisSerializer implements RedisSerializer<Object> {

    private final ObjectMapper om;

    public JsonRedisSerializer() {
        this.om = new ObjectMapper().enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }

    @Override
    public byte[] serialize(final Object t) throws SerializationException {
        try {
            return om.writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(final byte[] bytes) throws SerializationException {

        if(bytes == null){
            return null;
        }

        try {
            return om.readValue(bytes, Object.class);
        } catch (Exception e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }
}

