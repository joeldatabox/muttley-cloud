package br.com.muttley.redis.config;

import br.com.muttley.redis.property.MuttleyRedisProperty;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.redis.service.impl.RedisServiceImpl;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(MuttleyRedisProperty.class)
public class MuttleyRedisConfig implements InitializingBean {

    @Autowired
    private MuttleyRedisProperty property;

    @Bean
    public RedisService createService(@Autowired RedisTemplate redisTemplate) {
        return new RedisServiceImpl(property.getPrefixHash(), redisTemplate);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(property.getHost(), property.getPort()));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(@Autowired final RedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(@Autowired RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.create(connectionFactory);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerFactory.getLogger(MuttleyRedisConfig.class).info("Configuring cache with Redis on host \"" + property.getHost() + "\" with port \"" + property.getPort() + "\"");
    }
}
