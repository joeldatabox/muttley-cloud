package br.com.muttley.domain.service.impl;

import br.com.muttley.domain.service.ModelService;
import br.com.muttley.domain.service.impl.utils.MetadataAndIdModel;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryOwnerNotInformedException;
import br.com.muttley.model.BasicAggregateResultCount;
import br.com.muttley.model.Document;
import br.com.muttley.model.Model;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.mongo.service.infra.AggregationUtils;
import br.com.muttley.mongo.service.repository.CustomMongoRepository;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static br.com.muttley.model.security.domain.Domain.PUBLIC;
import static br.com.muttley.model.security.domain.Domain.RESTRICTED;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.data.mongodb.core.BulkOperations.BulkMode.UNORDERED;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public abstract class ModelServiceImpl<T extends Model> extends ServiceImpl<T> implements ModelService<T> {
    protected final CustomMongoRepository<T> repository;

    public ModelServiceImpl(final CustomMongoRepository<T> repository, final MongoTemplate mongoTemplate, final Class<T> clazz) {
        super(repository, mongoTemplate, clazz);
        this.repository = repository;
    }

    @Override
    public T save(final User user, final T value) {
        //verificando se realmente está criando um novo registro
        checkIdForSave(value);
        //setando o dono do registro
        value.setOwner(user);
        //garantindo que o metadata ta preenchido
        this.generateNewMetadataFor(user, value, null);
        //processa regra de negocio antes de qualquer validação
        this.beforeSave(user, value);
        //verificando precondições
        this.checkPrecondictionSave(user, value);
        //validando dados do objeto
        this.validator.validate(value);
        final T salvedValue = repository.save(user.getCurrentOwner(), value);
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
        this.generateNewMetadataFor(user, values, null);
        //processa regra de negocio antes de qualquer validação
        this.beforeSave(user, values);
        //verificando precondições
        this.checkPrecondictionSave(user, values);
        //validando dados do objeto
        this.validator.validateCollection(values);
        final Collection<T> otherValues = repository.save(user.getCurrentOwner(), values);
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
            throw this.createNotFoundExceptionById(user, value.getId());
            //throw new MuttleyNotFoundException(clazz, "id", "Registro não encontrado");
        }
        value.setOwner(user);
        //gerando metadata de alteração
        this.metadataService.generateMetaDataUpdateFor(user, this.repository.loadMetaData(user.getCurrentOwner(), value), value);
        //processa regra de negocio antes de qualquer validação
        this.beforeUpdate(user, value);
        //verificando precondições
        checkPrecondictionUpdate(user, value);
        //validando dados
        this.validator.validate(value);
        final T salvedValue = repository.save(user.getCurrentOwner(), value);
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

        final List<MetadataAndIdModel> metadatasAndIds = this.loadIdsAndMetadatasAndHisotricsFor(user, values);
        final Map<Boolean, List<T>> agroupedValues = values.stream()
                .collect(groupingBy(it -> {
                    final Optional<MetadataAndIdModel> itemOpt = metadatasAndIds
                            .parallelStream()
                            .filter(itMeta -> Objects.equals(it.getId(), itMeta.getId()))
                            .findFirst();
                    if (itemOpt.isPresent()) {
                        final MetadataAndIdModel metadataAndIdModel = itemOpt.get();
                        this.metadataService.generateMetaDataUpdateFor(user, metadataAndIdModel.getMetadata(), it);
                        return true;
                    }
                    return false;
                }));

        final List<T> valuesForSave = agroupedValues.get(Boolean.TRUE);

        if (!CollectionUtils.isEmpty(valuesForSave)) {
            //gerando metadata de alteração
            //valuesForSave.forEach(it -> generateMetaDataUpdate(user, it));
            //gerando histórico de alteração
            //valuesForSave.forEach(it -> it.setHistoric(generateHistoricUpdate(user, repository.loadHistoric(it))));
            //processa regra de negocio antes de qualquer validação
            beforeUpdate(user, valuesForSave);
            //verificando precondições
            checkPrecondictionUpdate(user, valuesForSave);
            //validando dados
            this.validator.validateCollection(valuesForSave);
            //criando o bulk para atualização em massa
            final BulkOperations operations = this.mongoTemplate.bulkOps(UNORDERED, clazz);
            valuesForSave.forEach(it -> {
                operations.updateOne(
                        new Query(
                                where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                        .and("_id").is(new ObjectId(it.getId()))
                        ),

                        Update.fromDBObject(new BasicDBObject("$set", it))
                );
            });
            operations.execute();
            //final Collection<T> otherValue = repository.save(user.getCurrentOwner(), valuesForSave);
            //realizando regras de enegocio depois do objeto ter sido alterado
            afterUpdate(user, valuesForSave);
        }
        final List<T> valuesNotSaved = agroupedValues.get(Boolean.FALSE);
        if (!CollectionUtils.isEmpty(valuesNotSaved)) {
            throw this.createNotFoundExceptionById(user, valuesNotSaved.parallelStream().map(Document::getId).collect(toList()));
            /*throw new MuttleyNotFoundException(clazz, "id", "Registros não encontrados")
                    .addDetails("ids", valuesNotSaved.parallelStream().map(Document::getId).collect(toList()));*/
        }
    }

    protected List<MetadataAndIdModel> loadIdsAndMetadatasAndHisotricsFor(final User user, final Collection<T> values) {
        /**
         * db.getCollection("contas-pagar").aggregate([
         *     {$match:{"owner.$id":ObjectId("60cc8953279e841c0974da56"), _id:{$in:[ObjectId("60cca012279e8437442bc81c"), ObjectId("60cca012279e8437442bc81d")]}}},
         *     {$project:{_id:1, metadata:1}}
         * ])
         */
        final AggregationResults<MetadataAndIdModel> ids = this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                .and("id").in(
                                        values.parallelStream().map(it -> it.getObjectId()).collect(toSet())
                                )),
                        project("id", "metadata")
                ),
                clazz, MetadataAndIdModel.class);
        if (ids == null || CollectionUtils.isEmpty(ids.getMappedResults())) {
            return Collections.emptyList();
        }
        return ids.getMappedResults();
    }

    @Override
    public T findById(final User user, final String id) {
        if (!ObjectId.isValid(id)) {
            throw new MuttleyBadRequestException(clazz, "id", "informe um id válido");
        }

        final T result = this.repository.findOne(user.getCurrentOwner(), id);
        if (isNull(result)) {
            throw this.createNotFoundExceptionById(user, id);
            //throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
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
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId()).and("id").is(new ObjectId(id))),
                        project("id", "owner")
                )
                , clazz, clazz);
        if (results == null || results.getUniqueMappedResult() == null) {
            throw this.createNotFoundExceptionById(user, id);
            //throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
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
        final Set<T> records = this.repository.findMulti(user.getCurrentOwner(), ids);
        if (records == null) {
            return Collections.emptySet();
        }
        return records;
    }

    @Override
    public T findFirst(final User user) {
        final T result = this.repository.findFirst(user.getCurrentOwner());
        if (isNull(result)) {
            throw this.createNotFoundExceptionById(user).setField("user");
            //throw new MuttleyNotFoundException(clazz, "user", "Nenhum registro encontrado");
        }
        return result;
    }

    @Override
    public void deleteById(final User user, final String id) {
        this.beforeDelete(user, id);
        checkPrecondictionDelete(user, id);
        if (!repository.exists(user.getCurrentOwner(), id)) {
            throw this.createNotFoundExceptionById(user, id);
            //throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
        }
        this.repository.delete(user.getCurrentOwner(), id);
        this.afterDelete(user, id);
    }

    @Override
    public void delete(final User user, final T value) {
        this.beforeDelete(user, value);
        checkPrecondictionDelete(user, value);
        if (!repository.exists(user.getCurrentOwner(), value)) {
            throw this.createNotFoundExceptionById(user, value.getId());
            //throw new MuttleyNotFoundException(clazz, "id", value.getId() + " este registro não foi encontrado");
        }
        this.repository.delete(user.getCurrentOwner(), value);
        this.afterDelete(user, value);
    }

    @Override
    public void checkPrecondictionDelete(final User user, final String id) {

    }

    @Override
    public void checkPrecondictionDelete(final User user, final T value) {

    }

    @Override
    public void beforeDelete(final User user, final T value) {

    }

    @Override
    public void beforeDelete(final User user, final String id) {

    }

    @Override
    public Long count(final User user, final Map<String, String> allRequestParams) {
        validateOwner(user.getCurrentOwner());


        final AggregationResults<BasicAggregateResultCount> result = mongoTemplate.aggregate(
                newAggregation(
                        AggregationUtils.createAggregationsCount(
                                this.entityMetaData,
                                this.getFilterAggregationOperationByWorkteam(user),
                                addOwnerQueryParam(user.getCurrentOwner(), allRequestParams),
                                "result"
                        )
                ),
                clazz, BasicAggregateResultCount.class);
        return result.getUniqueMappedResult() != null ? result.getUniqueMappedResult().getResult() : 0l;
    }

    @Override
    public List<T> findAll(final User user, final Map<String, String> allRequestParams) {
        //validando se o usuário tem owner informado
        this.validateOwner(user.getCurrentOwner());
        //perando o filtro

        final List<AggregationOperation> aggregateions = AggregationUtils.createAggregations(this.entityMetaData, null, addOwnerQueryParam(user.getCurrentOwner(), allRequestParams));
        //adicionando filtro para agregação
        aggregateions.addAll(this.getFilterAggregationOperationByWorkteam(user));

        final AggregationResults<T> results = mongoTemplate.aggregate(
                newAggregation(aggregateions), clazz, clazz
        );

        if (results == null || CollectionUtils.isEmpty(results.getMappedResults())) {
            throw new MuttleyNoContentException(clazz, "user", "não foi encontrado nenhum registro");
        }
        return results.getMappedResults();
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


    private final void validateOwner(final Owner owner) {
        if (owner == null) {
            throw new MuttleyRepositoryOwnerNotInformedException(this.clazz);
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

    protected List<AggregationOperation> getFilterAggregationOperationByWorkteam(final User user) {
        //se for o owner do sistema não faz sentido colocar restrição pois ele tem acesso a tudo
        if (user.isOwner()) {
            return Collections.emptyList();
        }
        return asList(
                match(
                        new Criteria().orOperator(
                                //pegando todos os registros que forem publicos
                                where("metadata.domain").is(PUBLIC),
                                //pegando todos os registros que o proprio usuário criou
                                where("metadata.historic.createdBy.$id").is(user.getObjectId()),
                                //pegando todos os registros de subordinados
                                where("metadata.historic.createdBy.$id")
                                        .in(
                                                user.getWorkTeamDomain()
                                                        .getSubordinates()
                                                        .parallelStream()
                                                        .map(it -> it.getUser().getObjectId())
                                                        .collect(toSet())
                                        ),
                                //pegando todos os registros dos colegas presentes no workteam
                                where("metadata.historic.createdBy.$id")
                                        .in(
                                                user.getWorkTeamDomain()
                                                        .getColleagues()
                                                        .parallelStream()
                                                        .map(it -> it.getUser().getObjectId())
                                                        .collect(toSet())
                                        ).and("metadata.domain").is(RESTRICTED)
                        )
                )
        );
    }

    protected List<Criteria> getFilterCriteriaByWorkteam(final User user) {
        if (user.isOwner()) {
            return null;
        }
        return asList(

                new Criteria().orOperator(
                        //pegando todos os registros que forem publicos
                        where("metadata.domain").is(PUBLIC),
                        //pegando todos os registros que o proprio usuário criou
                        where("metadata.historic.createdBy.$id").is(user.getObjectId()),
                        //pegando todos os registros de subordinados
                        where("metadata.historic.createdBy.$id")
                                .in(
                                        user.getWorkTeamDomain()
                                                .getSubordinates()
                                                .parallelStream()
                                                .map(it -> it.getUser().getObjectId())
                                                .collect(toSet())
                                ),
                        //pegando todos os registros dos colegas presentes no workteam
                        where("metadata.historic.createdBy.$id")
                                .in(
                                        user.getWorkTeamDomain()
                                                .getColleagues()
                                                .parallelStream()
                                                .map(it -> it.getUser().getObjectId())
                                                .collect(toSet())
                                ).and("metadata.domain").is(RESTRICTED)
                )
        );
    }
}
