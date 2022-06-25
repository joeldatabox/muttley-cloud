package br.com.muttley.domain.service.impl;

import br.com.muttley.domain.service.Service;
import br.com.muttley.domain.service.Validator;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.headers.services.MetadataService;
import br.com.muttley.model.Document;
import br.com.muttley.model.security.User;
import br.com.muttley.mongo.service.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static br.com.muttley.model.Document.getPropertyFrom;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public abstract class ServiceImpl<T extends Document> implements Service<T> {

    protected final DocumentMongoRepository<T> repository;
    protected final MongoTemplate mongoTemplate;
    protected final Class<T> clazz;
    protected final EntityMetaData entityMetaData;
    @Value("${muttley.security.check-roles:false}")
    private boolean checkRoles;

    @Autowired
    protected MetadataService metadataService;

    @Autowired
    protected Validator validator;

    public ServiceImpl(final DocumentMongoRepository<T> repository, final MongoTemplate mongoTemplate, final Class<T> clazz) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.clazz = clazz;
        this.entityMetaData = EntityMetaData.of(this.clazz);
    }

    @Override
    public boolean isCheckRole() {
        return this.checkRoles;
    }

    public String[] getBasicRoles() {
        return new String[]{""};
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
        checkIdForSave(value);
        //garantindo que o metadata ta preenchido
        this.metadataService.generateNewMetadataFor(user, value);
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
    public void checkPrecondictionSave(final User user, final Collection<T> values) {
        values.forEach(it -> this.checkPrecondictionSave(user, it));
    }

    @Override
    public void beforeSave(final User user, final Collection<T> values) {
        values.forEach(it -> this.beforeSave(user, it));
    }

    @Override
    public Collection<T> save(final User user, final Collection<T> values) {
        //verificando se realmente está criando um novo registro
        checkIdForSave(values);
        //garantindo que o metadata ta preenchido
        this.metadataService.generateNewMetadataFor(user, values);
        //processa regra de negocio antes de qualquer validação
        this.beforeSave(user, values);
        //verificando precondições
        this.checkPrecondictionSave(user, values);
        //validando dados do objeto
        this.validator.validateCollection(values);
        final Collection<T> otherValues = repository.save(values);
        //realizando regras de enegocio depois do objeto ter sido salvo
        this.afterSave(user, otherValues);
        //valor salvo
        return otherValues;
    }

    @Override
    public void saveOnly(final User user, final Collection<T> values) {
        this.save(user, values);
    }

    @Override
    public void afterSave(final User user, final Collection<T> values) {
        values.forEach(it -> this.afterSave(user, it));
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
        this.checkIdForUpdate(value);
        //verificando se o registro realmente existe
        if (!this.repository.exists(value.getId())) {
            throw new MuttleyNotFoundException(clazz, "id", "Registro não encontrado");
        }
        //gerando metadata de alteração
        this.metadataService.generateMetaDataUpdateFor(user, repository.loadMetadata(value), value);
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
    public void checkPrecondictionUpdate(final User user, final Collection<T> values) {
        values.forEach(it -> this.checkPrecondictionUpdate(user, it));
    }

    @Override
    public void beforeUpdate(final User user, final Collection<T> values) {
        values.forEach(it -> this.beforeUpdate(user, it));
    }

    @Override
    public void update(final User user, final Collection<T> values) {
        //verificando se realmente está alterando um registro
        this.checkIdForUpdate(values);
        //verificando se o registro realmente existe

        final Map<Boolean, List<T>> agroupedValues = values.stream()
                .collect(groupingBy(it -> this.repository.exists(it.getId())));

        final List<T> valuesForSave = agroupedValues.get(Boolean.TRUE);

        if (!CollectionUtils.isEmpty(valuesForSave)) {
            //gerando metadata de alteração
            valuesForSave.forEach(it -> this.metadataService.generateMetaDataUpdateFor(user, this.repository.loadMetadata(it), it));
            //processa regra de negocio antes de qualquer validação
            beforeUpdate(user, valuesForSave);
            //verificando precondições
            checkPrecondictionUpdate(user, valuesForSave);
            //validando dados
            this.validator.validateCollection(valuesForSave);
            final Collection<T> otherValue = repository.save(valuesForSave);
            //realizando regras de enegocio depois do objeto ter sido alterado
            afterUpdate(user, otherValue);
        }
        final List<T> valuesNotSaved = agroupedValues.get(Boolean.FALSE);
        if (!CollectionUtils.isEmpty(valuesNotSaved)) {
            throw new MuttleyNotFoundException(clazz, "id", "Registros não encontrados")
                    .addDetails("ids", valuesNotSaved.parallelStream().map(Document::getId).collect(toList()));
        }
    }

    @Override
    public void afterUpdate(final User user, final Collection<T> values) {
        values.forEach(it -> this.afterUpdate(user, it));
    }

    @Override
    public T findById(final User user, final String id) {
        if (!ObjectId.isValid(id)) {
            throw new MuttleyBadRequestException(clazz, "id", "informe um id válido");
        }

        final T result = this.repository.findOne(id);
        if (isNull(result)) {
            throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
        }
        return result;
    }

    @Override
    public T findReferenceById(User user, String id) {
        if (!ObjectId.isValid(id)) {
            throw new MuttleyBadRequestException(clazz, "id", "informe um id válido");
        }

        final AggregationResults<T> results = this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("id").is(new ObjectId(id))),
                        project("id")
                )
                , clazz, clazz);

        if (results == null || results.getUniqueMappedResult() == null) {
            throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
        }
        return results.getUniqueMappedResult();
    }

    @Override
    public Set<T> findByIds(final User user, final String[] ids) {
        if (ObjectUtils.isEmpty(ids)) {
            throw new MuttleyBadRequestException(clazz, "id", "informe pelo menos um id válido");
        }
        if (ids.length > 50) {
            throw new MuttleyBadRequestException(clazz, "ids", "Quantidade máxima excedida")
                    .addDetails("min", 1)
                    .addDetails("max", 50);
        }
        final Set<T> records = this.repository.findMulti(ids);
        if (records == null) {
            return Collections.emptySet();
        }
        return records;
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
        this.repository.delete(id);
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
    public Long count(final User user, final Map<String, String> allRequestParams) {
        return this.repository.count(allRequestParams);
    }

    @Override
    public List<T> findAll(final User user, final Map<String, String> allRequestParams) {
        final List<T> results = this.repository.findAll(allRequestParams);
        if (CollectionUtils.isEmpty(results)) {
            throw new MuttleyNoContentException(clazz, "user", "não foi encontrado nenhum registro");
        }
        return results;
    }

    @Override
    public boolean isEmpty(final User user) {
        return this.count(user, null) == 0l;
    }


    @Override
    public Object getPropertyValueFromId(final User user, final String id, final String property) {
        final Map<String, Object> map = new HashMap<>(1);
        map.put("id", id);
        return this.getPropertyValueFrom(user, map, property);
    }

    @Override
    public Object getPropertyValueFrom(final User user, final Map<String, Object> condictions, final String property) {
        //Criteria condiction = Criteria.where("owner.$id").is(user.getCurrentOwner().getObjectId());
        final AggregationResults<T> results = createAggregateForLoadProperties(user, condictions, property);
        return getPropertyFrom(results.getUniqueMappedResult(), property);
    }

    @Override
    public Object[] getPropertiesValueFrom(final User user, final Map<String, Object> condictions, final String... properties) {
        //Criteria condiction = Criteria.where("owner.$id").is(user.getCurrentOwner().getObjectId());
        final AggregationResults<T> results = createAggregateForLoadProperties(user, condictions, properties);

        final T result = results.getUniqueMappedResult();

        return Stream.of(properties)
                .map(it -> getPropertyFrom(result, it))
                .toArray(Object[]::new);
    }

    protected AggregationResults<T> createAggregateForLoadProperties(final User user, final Map<String, Object> condictions, final String... properties) {
        Criteria condiction = new Criteria();
        final Set<String> keysOfCondictions = condictions.keySet();

        for (final String key : keysOfCondictions) {
            condiction = condiction.and(key).is(condictions.get(key));
        }

        return this.mongoTemplate
                .aggregate(
                        newAggregation(
                                match(condiction),
                                project(properties),
                                limit(1)
                        ), clazz, clazz
                );
    }

    protected void checkIdForSave(final Collection<T> values) {
        values.parallelStream().forEach(it -> this.checkIdForSave(it));
    }

    protected void checkIdForSave(final T value) {
        if ("".equals(value.getId())) {
            value.setId(null);
        }
        if (value.getId() != null) {
            throw new MuttleyBadRequestException(clazz, "id", "Não é possível criar um registro com um id existente");
        }
    }

    protected void checkIdForUpdate(final Collection<T> values) {
        values.parallelStream().forEach(it -> this.checkIdForUpdate(it));
    }

    protected void checkIdForUpdate(final T value) {
        if (value.getId() == null) {
            throw new MuttleyBadRequestException(clazz, "id", "Não é possível alterar um registro sem informar um id válido");
        }
    }
}
