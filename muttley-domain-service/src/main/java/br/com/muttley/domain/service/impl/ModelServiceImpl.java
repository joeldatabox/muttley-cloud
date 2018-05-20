package br.com.muttley.domain.service.impl;

import br.com.muttley.domain.service.ModelService;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.Historic;
import br.com.muttley.model.Model;
import br.com.muttley.model.security.User;
import br.com.muttley.mongo.service.repository.CustomMongoRepository;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public abstract class ModelServiceImpl<T extends Model, ID extends ObjectId> extends ServiceImpl<T, ID> implements ModelService<T, ID> {
    final CustomMongoRepository<T, ID> repository;

    public ModelServiceImpl(final CustomMongoRepository<T, ID> repository, final Class<T> clazz) {
        super(repository, clazz);
        this.repository = repository;
    }

    @Override
    public T save(final User user, final T value) {
        //verificando se realmente está criando um novo registro
        if (value.getId() != null) {
            throw new MuttleyBadRequestException(clazz, "id", "Não é possível criar um registro com um id existente");
        }
        value.setOwner(user);
        //garantindo que o históriconão ficará nulo
        value.setHistoric(this.createHistoric(user));
        //validando dados
        this.validator.validate(value);
        //verificando precondições
        this.checkPrecondictionSave(user, value);
        return repository.save(user.getCurrentOwner(), value);
    }

    @Override
    public void checkPrecondictionSave(final User user, final T value) {

    }

    @Override
    public T update(final User user, final T value) {
        //verificando se realmente está alterando um registro
        if (value.getId() == null) {
            throw new MuttleyBadRequestException(clazz, "id", "Não é possível alterar um registro sem informar um id válido");
        }
        //verificando se o registro realmente existe
        if (!this.repository.exists((ID) value.getId())) {
            throw new MuttleyNotFoundException(clazz, "id", "Registro não encontrado");
        }
        value.setOwner(user);
        //gerando histórico de alteração
        value.setHistoric(generateHistoricUpdate(user, repository.loadHistoric(user.getCurrentOwner(), value)));
        //validando dados
        this.validator.validate(value);
        //verificando precondições
        checkPrecondictionUpdate(user, value);
        return repository.save(user.getCurrentOwner(), value);
    }

    @Override
    public void checkPrecondictionUpdate(final User user, final T value) {

    }

    @Override
    public T findById(final User user, final ID id) {
        if (isNull(id)) {
            throw new MuttleyBadRequestException(clazz, "id", "informe um id válido");
        }

        final T result = this.repository.findOne(user.getCurrentOwner(), id);
        if (isNull(result)) {
            throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
        }
        return result;
    }

    @Override
    public T findFirst(final User user) {
        final T result = this.repository.findFirst(user.getCurrentOwner());
        if (isNull(result)) {
            throw new MuttleyNotFoundException(clazz, "user", "Nenhum registro encontrado");
        }
        return result;
    }

    @Override
    public Historic loadHistoric(final User user, final ID id) {
        final Historic historic = repository.loadHistoric(user.getCurrentOwner(), id);
        if (isNull(historic)) {
            throw new MuttleyNotFoundException(clazz, "historic", "Nenhum registro encontrado");
        }
        return historic;
    }

    @Override
    public Historic loadHistoric(final User user, final T value) {
        final Historic historic = repository.loadHistoric(user.getCurrentOwner(), value);
        if (isNull(historic)) {
            throw new MuttleyNotFoundException(clazz, "historic", "Nenhum registro encontrado");
        }
        return historic;
    }

    @Override
    public void deleteById(final User user, final ID id) {
        checkPrecondictionDelete(user, id);
        if (!repository.exists(user.getCurrentOwner(), id)) {
            throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
        }
        this.repository.delete(user.getCurrentOwner(), id);
        beforeDelete(user, id);
    }

    @Override
    public void delete(final User user, final T value) {
        checkPrecondictionDelete(user, (ID) value.getId());
        if (!repository.exists(user.getCurrentOwner(), value)) {
            throw new MuttleyNotFoundException(clazz, "id", value.getId() + " este registro não foi encontrado");
        }
        this.repository.delete(user.getCurrentOwner(), value);
        beforeDelete(user, value);
    }

    @Override
    public void checkPrecondictionDelete(final User user, final ID id) {

    }

    @Override
    public void beforeDelete(final User user, final T value) {

    }

    @Override
    public void beforeDelete(final User user, final ID id) {

    }

    @Override
    public Long count(final User user, final Map<String, Object> allRequestParams) {
        return this.repository.count(user.getCurrentOwner(), allRequestParams);
    }

    @Override
    public List<T> findAll(final User user, final Map<String, Object> allRequestParams) {
        final List<T> results = this.repository.findAll(user.getCurrentOwner(), allRequestParams);
        if (isEmpty(results)) {
            throw new MuttleyNoContentException(clazz, "user", "não foi encontrado nenhum registro");
        }
        return results;
    }

    /**
     * Valida se ouve algum furo no processo de negociocio que venha a se alterar o dono do registro
     */
    private final void checkOwner(final User user, final T value) {
        final Model other = findById(user, (ID) value.getId());
        //não pode-se alterar o usuário
        if (!other.getOwner().equals(user.getCurrentOwner())) {
            throw new MuttleyBadRequestException(clazz, "user", "não é possível fazer a alteração do usuário dono do registro");
        }
    }
}
