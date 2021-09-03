package br.com.muttley.localcache.services;

import br.com.muttley.model.Model;
import br.com.muttley.model.security.User;

/**
 * @author Joel Rodrigues Moreira on 02/09/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface LocalModelService<T extends Model> {

    boolean containsInCahce(final User user, final Class<T> clazz, final String key);

    boolean containsReferenceInCahce(final User user, final Class<T> clazz, final String key);

    LocalModelService<T> addCache(final User user, T value, final String key);

    LocalModelService<T> addCache(final User user, T value, final String key, final long timeout);

    LocalModelService<T> addReferenceCache(final User user, T value, final String key);

    LocalModelService<T> addReferenceCache(final User user, T value, final String key, final long timeout);

    T loadModel(final User user, final Class<T> clazz, final String key);

    T loadReference(final User user, final Class<T> clazz, final String key);

    LocalModelService<T> expire(final User user, final Class<T> clazz, final String key);

    LocalModelService<T> expireReference(final User user, final Class<T> clazz, final String key);

    LocalModelService<T> refreshExpire(final User user, Class<T> clazz, final String key);

    LocalModelService<T> refreshExpireReference(final User user, Class<T> clazz, final String key);
}
