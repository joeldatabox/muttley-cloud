package br.com.muttley.domain.service.impl;

import br.com.muttley.domain.service.ModelService;
import br.com.muttley.domain.service.impl.utils.MetadataAndHistoricIdModel;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.Model;
import br.com.muttley.model.security.User;
import br.com.muttley.mongo.service.repository.CustomMongoRepository;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
        this.createMetaData(user, values);
        //garantindo que o históriconão ficará nulo
        values.parallelStream().forEach(it -> it.setHistoric(this.createHistoric(user)));
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
            throw new MuttleyNotFoundException(clazz, "id", "Registro não encontrado");
        }
        value.setOwner(user);
        //gerando histórico de alteração
        value.setHistoric(generateHistoricUpdate(user, repository.loadHistoric(user.getCurrentOwner(), value)));
        //gerando metadata de alteração
        this.generateMetaDataUpdate(user, value);
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

        final List<MetadataAndHistoricIdModel> metadatasAndHistorics = this.loadIdsAndMetadatasAndHisotricsFor(user, values);
        final Map<Boolean, List<T>> agroupedValues = values.stream()
                .collect(groupingBy(it -> {
                    final Optional<MetadataAndHistoricIdModel> itemOpt = metadatasAndHistorics
                            .parallelStream()
                            .filter(itMeta -> Objects.equals(it.getId(), itMeta.getId()))
                            .findFirst();
                    if (itemOpt.isPresent()) {
                        final MetadataAndHistoricIdModel metadataAndHistoricIdModel = itemOpt.get();
                        this.generateMetaDataUpdate(user, metadataAndHistoricIdModel.getMetadata(), it);
                        it.setHistoric(this.generateHistoricUpdate(user, metadataAndHistoricIdModel.getHistoric()));
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
            throw new MuttleyNotFoundException(clazz, "id", "Registros não encontrados")
                    .addDetails("ids", valuesNotSaved.parallelStream().map(Document::getId).collect(toList()));
        }
    }

    private List<MetadataAndHistoricIdModel> loadIdsAndMetadatasAndHisotricsFor(final User user, final Collection<T> values) {
        /**
         * db.getCollection("contas-pagar").aggregate([
         *     {$match:{"owner.$id":ObjectId("60cc8953279e841c0974da56"), _id:{$in:[ObjectId("60cca012279e8437442bc81c"), ObjectId("60cca012279e8437442bc81d")]}}},
         *     {$project:{_id:1, metadata:1, historic:1}}
         * ])
         */
        final AggregationResults<MetadataAndHistoricIdModel> ids = this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                .and("id").in(
                                        values.parallelStream().map(it -> it.getObjectId()).collect(toSet())
                                )),
                        project("id", "metadata", "historic")
                ),
                clazz, MetadataAndHistoricIdModel.class);
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
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId()).and("id").is(new ObjectId(id))),
                        project("id", "owner")
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
            throw new MuttleyNotFoundException(clazz, "user", "Nenhum registro encontrado");
        }
        return result;
    }

    @Override
    public Historic loadHistoric(final User user, final String id) {
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
    public void deleteById(final User user, final String id) {
        this.beforeDelete(user, id);
        checkPrecondictionDelete(user, id);
        if (!repository.exists(user.getCurrentOwner(), id)) {
            throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
        }
        this.repository.delete(user.getCurrentOwner(), id);
        this.afterDelete(user, id);
    }

    @Override
    public void delete(final User user, final T value) {
        this.beforeDelete(user, value);
        checkPrecondictionDelete(user, value.getId());
        if (!repository.exists(user.getCurrentOwner(), value)) {
            throw new MuttleyNotFoundException(clazz, "id", value.getId() + " este registro não foi encontrado");
        }
        this.repository.delete(user.getCurrentOwner(), value);
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
        return this.repository.count(user.getCurrentOwner(), allRequestParams);
    }

    @Override
    public List<T> findAll(final User user, final Map<String, String> allRequestParams) {
        final List<T> results = this.repository.findAll(user.getCurrentOwner(), allRequestParams);
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

}
