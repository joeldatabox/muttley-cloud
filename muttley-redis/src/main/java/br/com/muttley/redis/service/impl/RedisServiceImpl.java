package br.com.muttley.redis.service.impl;

import br.com.muttley.redis.model.MuttleyRedisWrapper;
import br.com.muttley.redis.service.RedisService;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

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
    public Set<String> getKey() {
        return this.redisTemplate.keys(this.getBasicKey() + "*");
    }

    @Override
    public Set<String> getKeys(final String expression) {
        return this.redisTemplate.keys(this.getBasicKey() + "*" + expression);
    }

    @Override
    public RedisService set(final String key, final T value) {
        //redisTemplate.opsForHash().put(getBasicKey(), key, value);
        redisTemplate.opsForValue().set(createKey(key), value);
        return this;
    }

    @Override
    public RedisService set(final String key, final T value, final Date date) {
        //caculando o tempo para expiração
        final long time = Duration.between(Instant.now(), date.toInstant()).getSeconds();
        //se o tempo for menor que 1, logo não precisamos salvar nada pois é sinal que já foi expirado
        if (time >= 1l) {
            this.set(key, value, time);
        }
        return this;
    }

    @Override
    public RedisService set(final String key, final T value, final long time) {
        final String keyValue = createKey(key);
        this.redisTemplate.opsForValue().set(createKey(key), value, time, SECONDS);
        return this;
    }

    @Override
    public RedisService changeKey(final String currentKey, final String newKey) {
        if (!currentKey.equals(newKey) && this.hasKey(currentKey)) {
            this.redisTemplate.rename(createKey(currentKey), createKey(newKey));
        }
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
    public RedisService deleteByExpression(final String expression) {
        this.redisTemplate.delete(this.getKeys(expression));
        return this;
    }

    @Override
    public Collection list() {
        return this.redisTemplate.opsForValue().multiGet(this.redisTemplate.keys(getBasicKey() + "*"));
    }

    @Override
    public List getByExpression(final String expression) {
        return this.redisTemplate.opsForValue().multiGet(this.redisTemplate.keys(getBasicKey() + "*" + expression));
    }

    @Override
    public RedisService clearAll() {
        this.redisTemplate.delete(this.redisTemplate.keys(getBasicKey() + "*"));
        return this;
    }

    @Override
    public boolean hasKey(final String key) {
        return this.redisTemplate.hasKey(createKey(key));
    }

    @Override
    public boolean hasKeyByExpression(final String expression) {
        return !CollectionUtils.isEmpty(this.redisTemplate.keys(this.getBasicKey() + "*" + expression));
    }

    private String createKey(final String key) {
        return this.getBasicKey() + ":" + key;
    }
}

class JsonRedisSerializer implements RedisSerializer<Object> {

    @Override
    public byte[] serialize(final Object value) throws SerializationException {
        try {
            return getObjectMapper().writeValueAsBytes(new MuttleyRedisWrapper<>(value));
        } catch (final JsonProcessingException e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(final byte[] bytes) throws SerializationException {
        if (bytes == null) {
            return null;
        }
        try {
            return getObjectMapper().readValue(bytes, MuttleyRedisWrapper.class).getContent();
        } catch (final Exception e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }

    private static ObjectMapper getObjectMapper() {
        return new ObjectMapper()
                .enableDefaultTyping()
                .enableDefaultTyping(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, JsonTypeInfo.As.PROPERTY)
                .registerModule(
                        new SimpleModule("ObjectId",
                                new Version(1, 0, 0, null, null, null)
                        ).addSerializer(ObjectId.class, new ObjectIdSerializer())
                ).registerModule(
                        new SimpleModule("ZonedDateTime",
                                new Version(1, 0, 0, null, null, null)
                        ).addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer())
                                .addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer())
                )
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setVisibility(FIELD, ANY);
    }
}

