package br.com.muttley.domain.impl;

import br.com.muttley.domain.Service;
import br.com.muttley.domain.Validator;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.security.User;
import br.com.muttley.mongo.repository.SimpleTenancyMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public abstract class ServiceImpl<T extends Document> implements Service<T> {

    protected final SimpleTenancyMongoRepository<T> repository;
    protected final Class<T> clazz;

    @Autowired
    protected Validator validator;

    public ServiceImpl(final SimpleTenancyMongoRepository<T> repository, final Class<T> clazz) {
        this.repository = repository;
        this.clazz = clazz;
    }

    @Override
    public void checkPrecondictionSave(final User user, final T value) {
    }

    @Override
    public void beforeSave(final User user, final T value) {
    }

    @Override
    public T save(final User user, final T value) {
        //verificando se realmente está criando um novo registro
        if (value.getId() != null) {
            throw new MuttleyBadRequestException(clazz, "id", "Não é possível criar um registro com um id existente");
        }
        //garantindo que o históriconão ficará nulo
        value.setHistoric(this.createHistoric(user));
        //processa regra de negocio antes de qualquer validação
        this.beforeSave(user, value);
        //verificando precondições
        this.checkPrecondictionSave(user, value);
        //validando dados do objeto
        this.validator.validate(value);
        final T otherValue = repository.save(value);
        //realizando regras de enegocio depois do objeto ter sido salvo
        this.afterSave(user, otherValue);
        //valor salvo
        return otherValue;
    }

    @Override
    public void afterSave(final User user, final T value) {
    }

    @Override
    public void checkPrecondictionUpdate(final User user, final T value) {

    }

    @Override
    public void beforeUpdate(final User user, final T value) {

    }

    @Override
    public T update(final User user, final T value) {
        //verificando se realmente está alterando um registro
        if (value.getId() == null) {
            throw new MuttleyBadRequestException(clazz, "id", "Não é possível alterar um registro sem informar um id válido");
        }
        //verificando se o registro realmente existe
        if (!this.repository.exists(value.getId())) {
            throw new MuttleyNotFoundException(clazz, "id", "Registro não encontrado");
        }
        //gerando histórico de alteração
        value.setHistoric(generateHistoricUpdate(user, repository.loadHistoric(value)));
        //processa regra de negocio antes de qualquer validação
        beforeUpdate(user, value);
        //verificando precondições
        checkPrecondictionUpdate(user, value);
        //validando dados
        this.validator.validate(value);
        final T otherValue = repository.save(value);
        //realizando regras de enegocio depois do objeto ter sido alterado
        afterUpdate(user, value);
        return otherValue;
    }

    @Override
    public void afterUpdate(final User user, final T value) {

    }

    @Override
    public T findById(final User user, final String id) {
        if (isNull(id)) {
            throw new MuttleyBadRequestException(clazz, "id", "informe um id válido");
        }

        final Optional<T> result = this.repository.findById(id);
        if (!result.isPresent()) {
            throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
        }
        return result.get();
    }

    @Override
    public T findFirst(final User user) {
        final T result = this.repository.findFirst();
        if (isNull(result)) {
            throw new MuttleyNotFoundException(clazz, "user", "Nenhum registro encontrado");
        }
        return result;
    }

    @Override
    public Historic loadHistoric(final User user, final String id) {
        final Historic historic = this.repository.loadHistoric(id);
        if (isNull(historic)) {
            throw new MuttleyNotFoundException(clazz, "historic", "Nenhum registro encontrado");
        }
        return historic;
    }

    @Override
    public Historic loadHistoric(final User user, final T value) {
        return this.loadHistoric(user, value.getId());
    }

    @Override
    public void checkPrecondictionDelete(final User user, final String id) {

    }

    @Override
    public void beforeDelete(final User user, final String id) {

    }

    @Override
    public void deleteById(final User user, final String id) {
        beforeDelete(user, id);
        checkPrecondictionDelete(user, id);
        if (!repository.exists(id)) {
            throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
        }
        this.repository.deleteById(id);
        afterDelete(user, id);
    }

    @Override
    public void afterDelete(final User user, final String id) {

    }

    @Override
    public void beforeDelete(final User user, final T value) {

    }

    @Override
    public void delete(final User user, final T value) {
        beforeDelete(user, value);
        checkPrecondictionDelete(user, value.getId());
        if (!repository.exists(value)) {
            throw new MuttleyNotFoundException(clazz, "id", value.getId() + " este registro não foi encontrado");
        }
        this.repository.delete(value);
        afterDelete(user, value);
    }

    @Override
    public void afterDelete(final User user, final T value) {

    }

    @Override
    public Long count(final User user, final Map<String, Object> allRequestParams) {
        return this.repository.count(allRequestParams);
    }

    @Override
    public boolean exists(User user, T value) {
        return this.repository.exists(value);
    }

    @Override
    public boolean exists(User user, String id) {
        return this.repository.exists("_id", id);
    }

    @Override
    public List<T> findAll(final User user, final Map<String, Object> allRequestParams) {
        final List<T> results = this.repository.findAll(allRequestParams);
        if (CollectionUtils.isEmpty(results)) {
            throw new MuttleyNoContentException(clazz, "user", "não foi encontrado nenhum registro");
        }
        return results;
    }

    protected Historic createHistoric(final User user) {
        final Date now = new Date();
        return new Historic()
                .setCreatedBy(user)
                .setDtCreate(now)
                .setLastChangeBy(user)
                .setDtChange(now);
    }

    protected Historic generateHistoricUpdate(final User user, final Historic historic) {
        return historic
                .setLastChangeBy(user)
                .setDtChange(new Date());
    }

    @Override
    public boolean isEmpty(final User user) {
        return this.count(user, null) == 0l;
    }
}
