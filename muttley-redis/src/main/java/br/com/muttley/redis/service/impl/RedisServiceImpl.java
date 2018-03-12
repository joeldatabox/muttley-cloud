package br.com.muttley.redis.service.impl;

import br.com.muttley.redis.service.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 08/01/18.
 * @project demo
 */
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOps;
    private final String basicKey;


    public RedisServiceImpl(final String basicKey, final RedisTemplate redisTemplate) {
        this.basicKey = basicKey;
        this.redisTemplate = redisTemplate;
        //this.redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        this.redisTemplate.setHashValueSerializer(new JsonRedisSerializer());
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
    public void set(final Map value, final long time) {
        this.set(String.valueOf(value.hashCode()), value, time);
    }

    @Override
    public void set(final String key, final Object value) {
        this.hashOps.put(this.getBasicKey(), key, value);
    }

    @Override
    public void set(final String key, final Map value, final long time) {
        final String keyValue = getBasicKey() + ":" + key;
        this.hashOps.putAll(keyValue, value);
        this.redisTemplate.expireAt(
                keyValue,
                Date.from(
                        Instant.now()
                                .plus(time, ChronoUnit.MILLIS)
                )
        );
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

class JsonRedisSerializer implements RedisSerializer<Object> {
    private final ObjectMapper om;

    public JsonRedisSerializer() {
        this.om = new ObjectMapper().configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
    }

    @Override
    public byte[] serialize(final Object value) throws SerializationException {
        try {
            return om.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(final byte[] bytes) throws SerializationException {
        if (bytes == null) {
            return null;
        }

        try {
            return om.readValue(bytes, Object.class);
        } catch (Exception e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }
}