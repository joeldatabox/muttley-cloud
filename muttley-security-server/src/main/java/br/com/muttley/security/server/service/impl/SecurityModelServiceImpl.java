package br.com.muttley.security.server.service.impl;

import br.com.muttley.domain.service.impl.ModelServiceImpl;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryIdIsNullException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryInvalidIdException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryOwnerNotInformedException;
import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.Model;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.mongo.service.infra.AggregationUtils;
import br.com.muttley.mongo.service.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import br.com.muttley.security.server.service.SecurityService;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;
import static org.bson.types.ObjectId.isValid;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 02/08/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class SecurityModelServiceImpl<T extends Model> extends ModelServiceImpl<T> implements SecurityService<T> {
    protected final DocumentMongoRepository<T> repository;
    private final EntityMetaData entityMetaData;

    public SecurityModelServiceImpl(final DocumentMongoRepository<T> repository, final MongoTemplate mongoTemplate, final Class<T> clazz) {
        super(null, mongoTemplate, clazz);
        this.repository = repository;
        this.entityMetaData = EntityMetaData.of(clazz);
    }

    @Override
    public T save(final User user, final T value) {
        //verificando se realmente está criando um novo registro
        checkIdForSave(value);
        //setando o dono do registro
        value.setOwner(user);
        //garantindo que o históriconão ficará nulo
        value.setHistoric(this.createHistoric(user));
        //garantindo que o metadata ta preenchido
        this.createMetaData(user, value);
        //processa regra de negocio antes de qualquer validação
        this.beforeSave(user, value);
        //verificando precondições
        this.checkPrecondictionSave(user, value);
        //validando dados do objeto
        this.validator.validate(value);
        final T salvedValue = this.saveByTemplate(user.getCurrentOwner(), value);
        //realizando regras de enegocio depois do objeto ter sido salvo
        this.afterSave(user, salvedValue);
        //valor salvo
        return salvedValue;
    }

    @Override
    public void checkPrecondictionSave(final User user, final T value) {

    }

    @Override
    public Collection<T> save(final User user, final Collection<T> values) {
        //verificando se realmente está criando um novo registro
        checkIdForSave(values);
        //garantindo que o metadata ta preenchido
        this.createMetaData(user, values);
        //garantindo que o históriconão ficará nulo
        values.parallelStream().forEach(it -> it.setHistoric(this.createHistoric(user)));
        //processa regra de negocio antes de qualquer validação
        this.beforeSave(user, values);
        //verificando precondições
        this.checkPrecondictionSave(user, values);
        //validando dados do objeto
        this.validator.validateCollection(values);
        //final Collection<T> otherValues = repository.save(user.getCurrentOwner(), values);
        final Collection<T> otherValues = this.saveByTemplate(user.getCurrentOwner(), values);
        //realizando regras de enegocio depois do objeto ter sido salvo
        this.afterSave(user, otherValues);
        //valor salvo
        return otherValues;
    }

    @Override
    public T update(final User user, final T value) {
        //verificando se realmente está alterando um registro
        if (value.getId() == null) {
            throw new MuttleyBadRequestException(clazz, "id", "Não é possível alterar um registro sem informar um id válido");
        }
        //verificando se o registro realmente existe
        if (!this.repository.exists(value)) {
            throw new MuttleyNotFoundException(clazz, "id", "Registro não encontrado");
        }
        value.setOwner(user);
        //gerando histórico de alteração
        //value.setHistoric(generateHistoricUpdate(user, repository.loadHistoric(user.getCurrentOwner(), value)));
        value.setHistoric(generateHistoricUpdate(user, this.loadHistoric(user, value)));
        //gerando metadata de alteração
        this.generateMetaDataUpdate(user, value);
        //processa regra de negocio antes de qualquer validação
        this.beforeUpdate(user, value);
        //verificando precondições
        checkPrecondictionUpdate(user, value);
        //validando dados
        this.validator.validate(value);
        final T salvedValue = this.saveByTemplate(user.getCurrentOwner(), value);
        afterUpdate(user, salvedValue);
        return salvedValue;
    }

    @Override
    public void checkPrecondictionUpdate(final User user, final T value) {

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
            valuesForSave.forEach(it -> generateMetaDataUpdate(user, it));
            //gerando histórico de alteração
            valuesForSave.forEach(it -> it.setHistoric(generateHistoricUpdate(user, repository.loadHistoric(it))));
            //processa regra de negocio antes de qualquer validação
            beforeUpdate(user, valuesForSave);
            //verificando precondições
            checkPrecondictionUpdate(user, valuesForSave);
            //validando dados
            this.validator.validateCollection(valuesForSave);
            final Collection<T> otherValue = this.saveByTemplate(user.getCurrentOwner(), valuesForSave);
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
    public T findById(final User user, final String id) {
        if (isNull(id)) {
            throw new MuttleyBadRequestException(clazz, "id", "informe um id válido");
        }

        final T result = this.mongoTemplate.findById(new ObjectId(id), this.clazz);
        if (isNull(result)) {
            throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
        }
        return result;
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

        //final Set<T> records = this.repository.findMulti(user.getCurrentOwner(), ids);
        final Set<T> records = this.findMultByTemplate(user.getCurrentOwner(), ids);
        if (records == null) {
            return Collections.emptySet();
        }
        return records;
    }

    @Override
    public T findFirst(final User user) {
        final List<T> result = this.mongoTemplate.find(new Query().limit(1), this.clazz);
        if (isNull(result) || result.isEmpty()) {
            throw new MuttleyNotFoundException(clazz, "user", "Nenhum registro encontrado");
        }
        return result.get(0);
    }

    @Override
    public Historic loadHistoric(final User user, final String id) {
        final Historic historic = this.loadHistoricByTemplate(user.getCurrentOwner(), id);
        if (isNull(historic)) {
            throw new MuttleyNotFoundException(clazz, "historic", "Nenhum registro encontrado");
        }
        return historic;
    }

    @Override
    public Historic loadHistoric(final User user, final T value) {
        final Historic historic = this.loadHistoricByTemplate(user.getCurrentOwner(), value.getId());
        if (isNull(historic)) {
            throw new MuttleyNotFoundException(clazz, "historic", "Nenhum registro encontrado");
        }
        return historic;
    }

    @Override
    public void deleteById(final User user, final String id) {
        this.beforeDelete(user, id);
        checkPrecondictionDelete(user, id);
        if (!repository.exists(user.getCurrentOwner(), id)) {
            throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
        }
        //this.repository.delete(user.getCurrentOwner(), id);
        this.mongoTemplate.remove(
                new Query(
                        where("owner.$id").is(user.getCurrentOwner().getObjectId()).and("_id").is(newObjectId(id))
                ), this.clazz
        );
        this.afterDelete(user, id);
    }

    @Override
    public void delete(final User user, final T value) {
        this.beforeDelete(user, value);
        checkPrecondictionDelete(user, value.getId());
        if (!repository.exists(user.getCurrentOwner(), value)) {
            throw new MuttleyNotFoundException(clazz, "id", value.getId() + " este registro não foi encontrado");
        }
        //this.repository.delete(user.getCurrentOwner(), value);
        this.mongoTemplate.remove(
                new Query(
                        where("owner.$id").is(user.getCurrentOwner().getObjectId()).and("_id").is(value.getObjectId())
                ), this.clazz
        );
        this.afterDelete(user, value);
    }

    @Override
    public void checkPrecondictionDelete(final User user, final String id) {

    }

    @Override
    public void beforeDelete(final User user, final T value) {

    }

    @Override
    public void beforeDelete(final User user, final String id) {

    }

    @Override
    public Long count(final User user, final Map<String, String> allRequestParams) {
        //return this.repository.count(user.getCurrentOwner(), allRequestParams);
        return this.countByTemplate(user.getCurrentOwner(), allRequestParams);
    }

    @Override
    public List<T> findAll(final User user, final Map<String, String> allRequestParams) {
        //final List<T> results = this.repository.findAll(user.getCurrentOwner(), allRequestParams);
        final List<T> results = this.findAllByTemplate(user.getCurrentOwner(), allRequestParams);
        if (CollectionUtils.isEmpty(results)) {
            throw new MuttleyNoContentException(clazz, "user", "não foi encontrado nenhum registro");
        }
        return results;
    }

    @Override
    protected AggregationResults<T> createAggregateForLoadProperties(final User user, final Map<String, Object> condictions, final String... properties) {
        condictions.put("owner.$id", user.getCurrentOwner().getObjectId());
        return super.createAggregateForLoadProperties(user, condictions, properties);
    }

    /**
     * Valida se ouve algum furo no processo de negociocio que venha a se alterar o dono do registro
     */
    private final void checkOwner(final User user, final T value) {
        final Model other = findById(user, value.getId());
        //não pode-se alterar o usuário
        if (!other.getOwner().equals(user.getCurrentOwner())) {
            throw new MuttleyBadRequestException(clazz, "user", "não é possível fazer a alteração do usuário dono do registro");
        }
    }

    private T saveByTemplate(final Owner owner, final T value) {
        validateOwner(owner);
        value.setOwner(owner);
        //salvando o registro
        this.mongoTemplate.save(value);
        //pegando o registro salvo
        final AggregationResults<T> results = mongoTemplate.aggregate(
                newAggregation(
                        asList(
                                sort(DESC, "_id"),
                                limit(1)
                        )
                ), this.clazz,
                this.clazz
        );
        if (results == null || results.getUniqueMappedResult() == null) {
            throw new MuttleyException();
        }
        return results.getUniqueMappedResult();
    }

    /*private T updateByTemplate(final Owner owner, final T value) {
        validateOwner(owner);
        value.setOwner(owner);
        //salvando o registro
        this.mongoTemplate.save(value);
        //pegando o registro salvo
        final AggregationResults<T> results = mongoTemplate.aggregate(
                newAggregation(
                        asList(
                                sort(DESC, "_id"),
                                limit(1)
                        )
                ), this.clazz,
                this.clazz
        );
        if (results == null || results.getUniqueMappedResult() == null) {
            throw new MuttleyException();
        }
        return results.getUniqueMappedResult();
    }*/

    private Collection<T> saveByTemplate(final Owner owner, final Collection<T> value) {
        return value.stream()
                .map(it -> this.saveByTemplate(owner, it))
                .collect(toList());
    }

    private final void validateOwner(final Owner owner) {
        if (owner == null) {
            throw new MuttleyRepositoryOwnerNotInformedException(this.clazz);
        }
    }

    private Historic loadHistoricByTemplate(final Owner owner, final String id) {
        final AggregationResults result = this.mongoTemplate
                .aggregate(
                        newAggregation(
                                match(where("owner.$id").is(owner.getObjectId())
                                        .and("_id").is(new ObjectId(id))
                                ), project().and("$historic.createdBy").as("createdBy")
                                        .and("$historic.dtCreate").as("dtCreate")
                                        .and("$historic.dtChange").as("dtChange")
                        ), this.clazz, Historic.class);

        return result.getUniqueMappedResult() != null ? ((Historic) result.getUniqueMappedResult()) : null;
    }


    private MetadataDocument loadMetaDataByTemplate(final Owner owner, final String id) {
        final AggregationResults result = this.mongoTemplate
                .aggregate(
                        newAggregation(
                                match(where("owner.$id").is(owner.getObjectId())
                                        .and("_id").is(new ObjectId(id))
                                ), project().and("$metaData.timeZones").as("timeZones")
                                        .and("$metaData.versionDocument").as("versionDocument")
                        ), this.clazz, MetadataDocument.class);

        return result.getUniqueMappedResult() != null ? ((MetadataDocument) result.getUniqueMappedResult()) : null;
    }

    private Set<T> findMultByTemplate(final Owner owner, final String[] ids) {
        //criando um array de ObjecIds
        final ObjectId[] objectIds = of(ids)
                .parallel()
                .map(id -> {
                    try {
                        return newObjectId(id);
                    } catch (MuttleyRepositoryInvalidIdException ex) {
                        return null;
                    }
                    //pegando apenas ids válidos
                }).filter(Objects::nonNull)
                .collect(toSet())
                .stream()
                .toArray(ObjectId[]::new);

        //filtrando os ids válidos
        if (!ObjectUtils.isEmpty(objectIds)) {
            final List<T> records = this.mongoTemplate.find(
                    new Query(
                            where("owner.$id").is(owner.getObjectId())
                                    .and("id").in(objectIds)
                    ), this.clazz
            );

            if (CollectionUtils.isEmpty(records)) {
                return null;
            }

            return new HashSet<>(records);
        }

        return null;
    }

    private final List<T> findAllByTemplate(final Owner owner, final Map<String, String> queryParams) {
        validateOwner(owner);
        return this.mongoTemplate.aggregate(
                newAggregation(
                        AggregationUtils.createAggregations(this.entityMetaData, getBasicPipelines(this.clazz),
                                addOwnerQueryParam(owner, queryParams)
                        )
                ),
                this.clazz, this.clazz)
                .getMappedResults();
    }

    private final long countByTemplate(final Owner owner, final Map<String, String> queryParams) {
        validateOwner(owner);
        final AggregationResults result = this.mongoTemplate.aggregate(
                newAggregation(
                        AggregationUtils.createAggregationsCount(this.entityMetaData, getBasicPipelines(this.clazz),
                                addOwnerQueryParam(owner, queryParams)
                        )),
                this.clazz, ResultCount.class);
        return result.getUniqueMappedResult() != null ? ((ResultCount) result.getUniqueMappedResult()).getCount() : 0;
    }

    /**
     * Retorna uma lista de pipelines para agregação
     */
    List<AggregationOperation> getBasicPipelines(final Class<T> clazz) {
        return null;
    }

    private final ObjectId newObjectId(final String id) {
        this.validateId(id);
        return new ObjectId(id);
    }

    private final void validateId(final String id) {
        if (id == null) {
            throw new MuttleyRepositoryIdIsNullException(this.clazz);
        }
        if (!isValid(id)) {
            throw new MuttleyRepositoryInvalidIdException(this.clazz);
        }
    }

    private final Map<String, String> addOwnerQueryParam(final Owner owner, final Map<String, String> queryParams) {
        final Map<String, String> query = new LinkedHashMap<>(1);
        query.put("owner.$id.$is", owner.getObjectId().toString());
        if (queryParams != null) {
            query.putAll(queryParams);
        }
        return query;
    }

    protected final class ResultCount {
        private Long count;

        public Long getCount() {
            return count;
        }

        public ResultCount setCount(final Long count) {
            this.count = count;
            return this;
        }
    }

}
