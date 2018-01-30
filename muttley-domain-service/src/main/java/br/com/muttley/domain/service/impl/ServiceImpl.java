package br.com.muttley.domain.service.impl;

import br.com.muttley.domain.service.Service;
import br.com.muttley.model.Model;
import br.com.muttley.model.security.model.User;
import br.com.muttley.mongo.service.repository.CustomMongoRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public class ServiceImpl<T extends Model<ID>, ID extends Serializable> implements Service<T, ID> {

    private final CustomMongoRepository<T, ID> repository;
    private final Class<T> clazz;

    public ServiceImpl(final CustomMongoRepository<T, ID> repository, final Class<T> clazz) {
        this.repository = repository;
        this.clazz = clazz;
    }

    @Override
    public T save(final User user, final T value) {
        return null;
    }

    @Override
    public void checkPrecondictionSave(final User user, final T value) {

    }

    @Override
    public T update(final User user, final T value) {
        return null;
    }

    @Override
    public void checkPrecondictionUpdate(final User user, final T value) {

    }

    @Override
    public T findById(final User user, final ID id) {
        return null;
    }

    @Override
    public T findFirst(final User user) {
        return null;
    }

    @Override
    public void deleteById(final User user, final ID id) {

    }

    @Override
    public void delete(final User user, final T value) {

    }

    @Override
    public void checkPrecondictionDelete(final User user, final ID id) {

    }

    @Override
    public Long count(final User user, final Map<String, Object> allRequestParams) {
        return null;
    }

    @Override
    public List<T> findAll(final User user, final Map<String, Object> allRequestParams) {
        return null;
    }
}
