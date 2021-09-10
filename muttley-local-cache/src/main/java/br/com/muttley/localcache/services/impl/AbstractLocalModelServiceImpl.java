package br.com.muttley.localcache.services.impl;

import br.com.muttley.localcache.services.LocalModelService;
import br.com.muttley.model.Model;
import br.com.muttley.model.security.User;
import br.com.muttley.model.util.RedisUtils;
import br.com.muttley.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Joel Rodrigues Moreira on 02/09/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractLocalModelServiceImpl<T extends Model> implements LocalModelService<T> {
    private final RedisService redisService;
    private final long TIMEOUT = 25L;

    @Autowired
    public AbstractLocalModelServiceImpl(final RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public boolean containsInCahce(final User user, final Class<T> clazz, final String key) {
        return this.redisService.hasKey(this.getBasicKey(user, clazz, key));
    }

    @Override
    public boolean containsReferenceInCahce(User user, Class<T> clazz, String key) {
        return this.redisService.hasKey(this.getBasicKeyReference(user, clazz, key));
    }

    @Override
    public LocalModelService<T> addCache(final User user, final T value, final String key) {
        return this.addCache(user, value, key, TIMEOUT);
    }

    @Override
    public LocalModelService<T> addCache(final User user, final T value, final String key, long timeout) {
        this.redisService.set(this.getBasicKey(user, (Class<T>) value.getClass(), key), value, timeout);
        return this;
    }

    @Override
    public LocalModelService<T> addReferenceCache(User user, T value, String key) {
        return this.addReferenceCache(user, value, key, TIMEOUT);
    }

    @Override
    public LocalModelService<T> addReferenceCache(User user, T value, String key, long timeout) {
        this.redisService.set(this.getBasicKeyReference(user, (Class<T>) value.getClass(), key), value, timeout);
        return this;
    }

    @Override
    public T loadModel(final User user, final Class<T> clazz, String key) {
        final String basickey = this.getBasicKey(user, clazz, key);
        final T result;
        if (this.containsInCahce(basickey)) {
            result = (T) this.redisService.get(basickey);
            this.refreshExpire(user, clazz, key);
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public T loadReference(User user, Class<T> clazz, String key) {
        final String basickey = this.getBasicKeyReference(user, clazz, key);
        final T result;
        if (this.containsInCahce(basickey)) {
            result = (T) this.redisService.get(basickey);
            this.refreshExpire(user, clazz, key);
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public LocalModelService<T> expire(final User user, final Class<T> clazz, final String key) {
        this.redisService.delete(this.getBasicKey(user, clazz, key));
        this.redisService.delete(this.getBasicKeyReference(user, clazz, key));
        return this;
    }

    @Override
    public LocalModelService<T> refreshExpire(User user, Class<T> clazz, String key) {
        this.redisService.setExpire(this.getBasicKey(user, clazz, key), TIMEOUT);
        return this;
    }

    @Override
    public LocalModelService<T> refreshExpireReference(User user, Class<T> clazz, String key) {
        this.redisService.setExpire(this.getBasicKeyReference(user, clazz, key), TIMEOUT);
        return this;
    }

    protected boolean containsInCahce(final String key) {
        return this.redisService.hasKey(key);
    }

    protected String getBasicKey(final User user, final Class<T> clazz, final String key) {
        return RedisUtils.createKeyByOwner(user, clazz, key);
    }

    protected String getBasicKeyReference(final User user, final Class<T> clazz, final String key) {
        return RedisUtils.createKeyByOwner(user, clazz, "REFERENCE:" + key);
    }
}
