package br.com.muttley.redis.service.config;

import br.com.muttley.redis.service.RedisService;
import br.com.muttley.redis.service.impl.RedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Joel Rodrigues Moreira on 08/01/18.
 * @project demo
 */
@Configuration
public class RedisConfig {
    private final String host;
    private final int port;

    public RedisConfig(
            @Value("${muttley.redis.host:localhost}") final String host,
            @Value("${muttley.redis.port:6379}") final int port) {
        this.host = host;
        this.port = port;
    }

    @Bean
    public RedisService createService(@Value("${muttley.redis.prefixHash:muttley-cloud}") final String prefixHash,
                                      @Autowired RedisTemplate redisTemplate) {
        return new RedisServiceImpl(prefixHash, redisTemplate);
    }

    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.create(connectionFactory);
    }
}
