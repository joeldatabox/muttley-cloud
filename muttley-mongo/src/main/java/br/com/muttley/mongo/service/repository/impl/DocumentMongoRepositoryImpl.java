package br.com.muttley.mongo.service.repository.impl;

import br.com.muttley.annotations.index.CompoundIndexes;
import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryIdIsNullException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryInvalidIdException;
import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.mongo.service.infra.AggregationUtils;
import br.com.muttley.mongo.service.infra.metadata.EntityMetaData;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Stream.of;
import static org.bson.types.ObjectId.isValid;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class DocumentMongoRepositoryImpl<T extends Document> extends SimpleMongoRepository<T, String> implements br.com.muttley.mongo.service.repository.DocumentMongoRepository<T> {
    protected final MongoOperations operations;
    protected final Class<T> CLASS;
    protected final String COLLECTION;
    protected final EntityMetaData entityMetaData;
    protected final Log log = LogFactory.getLog(this.getClass());

    public DocumentMongoRepositoryImpl(final MongoEntityInformation<T, String> metadata, final MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.operations = mongoOperations;
        this.CLASS = metadata.getJavaType();
        this.COLLECTION = metadata.getCollectionName();
        this.entityMetaData = EntityMetaData.of(metadata.getJavaType());
        this.createIndexes(metadata);
    }

    @Override
    public Set<T> findMulti(final String[] ids) {

        //criando um array de ObjecIds
        final ObjectId[] objectIds = of(ids)
                .map(id -> {
                    try {
                        return newObjectId(id);
                    } catch (MuttleyRepositoryInvalidIdException ex) {
                        return null;
                    }
                    //pegando apenas ids válidos
                }).filter(Objects::nonNull).toArray(ObjectId[]::new);

        //filtrando os ids válidos
        if (!ObjectUtils.isEmpty(objectIds)) {
            final List<T> records = operations.find(
                    new Query(
                            where("id").in(objectIds)
                    ), CLASS
            );

            if (CollectionUtils.isEmpty(records)) {
                return null;
            }

            return new HashSet<>(records);
        }

        return null;
    }

    @Override
    public T findFirst() {
        return operations.findOne(new Query(), CLASS);
    }

    @Override
    public List<T> findAll(final Map<String, String> queryParams) {
        return operations
                .aggregate(
                        newAggregation(
                                AggregationUtils.createAggregations(this.entityMetaData, getBasicPipelines(this.CLASS),
                                        queryParams != null ? queryParams : new LinkedHashMap<>()
                                )
                        ),
                        COLLECTION, CLASS
                )
                .getMappedResults();
    }

    @Override
    public long count(final Map<String, String> queryParams) {
        final AggregationResults result = operations.aggregate(
                newAggregation(
                        AggregationUtils.createAggregationsCount(this.entityMetaData, getBasicPipelines(this.CLASS),
                                ((queryParams != null && !queryParams.isEmpty()) ? queryParams : new HashMap<>())
                        )
                ), COLLECTION, ResultCount.class);

        return result.getUniqueMappedResult() != null ? ((ResultCount) result.getUniqueMappedResult()).count : 0;
    }

    @Override
    public boolean exists(final T value) {
        return this.exists(value.getId());
    }

    @Override
    public boolean exists(final Map<String, Object> filter) {
        final Criteria criteria = new Criteria();
        filter.forEach((field, value) -> criteria.and(field).is(value));
        return operations.exists(new Query(criteria), CLASS);
    }

    @Override
    public boolean exists(final Object... filter) {
        if (filter.length % 2 != 0 || filter.length == 0) {
            throw new MuttleyException("O critétrios de filtro informado é inválido. Você passou o total de " + filter.length + " argumento(s)");
        }
        final Map<String, Object> filters = new HashMap<>();
        String lastField = null;
        for (int i = 0; i < filter.length; i++) {
            //verificando se o indice atual é um field ou um value
            if (i % 2 == 0) {
                lastField = (String) filter[i];
            } else {
                filters.put(lastField, filter[i]);
            }
        }
        return exists(filters);
    }

    @Override
    public Historic loadHistoric(final T value) {
        final AggregationResults result = operations.aggregate(
                newAggregation(
                        match(
                                where("_id").is(value.getObjectId())
                        ), project().and("$historic.createdBy").as("createdBy")
                                .and("$historic.dtCreate").as("dtCreate")
                                .and("$historic.dtChange").as("dtChange")
                                .and("$historic.lastChangeBy").as("lastChangeBy")


                ), COLLECTION, Historic.class);

        return result.getUniqueMappedResult() != null ? ((Historic) result.getUniqueMappedResult()) : null;
    }

    @Override
    public MetadataDocument loadMetadata(final T value) {
        final AggregationResults result = operations.aggregate(
                newAggregation(
                        match(
                                where("_id").is(value.getObjectId())
                        ), project().and("$metadata.timeZones").as("timeZones")
                                .and("$metadata.versionDocument").as("versionDocument")
                ), COLLECTION, MetadataDocument.class);

        return result.getUniqueMappedResult() != null ? ((MetadataDocument) result.getUniqueMappedResult()) : null;
    }

    @Override
    public Historic loadHistoric(final String id) {
        final AggregationResults result = operations.aggregate(
                newAggregation(
                        match(
                                where("_id").is(newObjectId(id))
                        ), project().and("$historic.createdBy").as("createdBy")
                                .and("$historic.dtCreate").as("dtCreate")
                                .and("$historic.dtChange").as("dtChange")
                                .and("$historic.lastChangeBy").as("lastChangeBy")


                ), COLLECTION, Historic.class);

        return result.getUniqueMappedResult() != null ? ((Historic) result.getUniqueMappedResult()) : null;
    }

    @Override
    public MetadataDocument loadMetadata(final String id) {
        final AggregationResults result = operations.aggregate(
                newAggregation(
                        match(
                                where("_id").is(newObjectId(id))
                        ), project().and("$metaData.timeZones").as("timeZones")
                                .and("$metaData.versionDocument").as("versionDocument")
                ), COLLECTION, MetadataDocument.class);

        return result.getUniqueMappedResult() != null ? ((MetadataDocument) result.getUniqueMappedResult()) : null;
    }

    /**
     * Retorna uma lista de pipelines para agregação
     */
    List<AggregationOperation> getBasicPipelines(final Class<T> clazz) {
        return null;
    }

    protected final void validateId(final String id) {
        if (id == null) {
            throw new MuttleyRepositoryIdIsNullException(this.CLASS);
        }
        if (!isValid(id)) {
            throw new MuttleyRepositoryInvalidIdException(this.CLASS);
        }
    }

    protected final ObjectId newObjectId(final String id) {
        this.validateId(id);
        return new ObjectId(id);
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

    private void createIndexes(final MongoEntityInformation<T, String> metadata) {
        final CompoundIndexes compoundIndexes = metadata.getJavaType().getAnnotation(CompoundIndexes.class);
        if (compoundIndexes != null) {
            final Supplier<Stream<CompoundIndex>> compoundIndexesValues = () -> Stream.of(compoundIndexes.value());
            //verificando se tem algum indices nas anotações com nome ou definições iguais
            final List<CompoundIndex> duplicateds = compoundIndexesValues.get()
                    .parallel()
                    .filter(it ->
                            compoundIndexesValues
                                    .get()
                                    .parallel()
                                    .filter(itt ->
                                            itt.name().equals(it.name()) || this.isEqualDefinitionIndex(BasicDBObject.parse(itt.def()), BasicDBObject.parse(it.def()))
                                    ).count() > 1
                    ).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(duplicateds)) {
                throw new MuttleyException("Existe definições de indices duplicados na classe -> " + this.CLASS.getCanonicalName())
                        .addDetails("duplicateds", duplicateds);
            }

            final List<DBObject> currentIdexies = this.operations.getCollection(metadata.getCollectionName()).getIndexInfo();
            final List<String> currentIndexiesName = currentIdexies
                    .parallelStream()
                    .map(index -> index.get("name"))
                    .map(Object::toString)
                    .collect(Collectors.toList());
            final DBCollection collection = this.operations.getCollection(metadata.getCollectionName());
            compoundIndexesValues.get()
                    .forEach(compoundIndex -> {
                        //verificando se já existe o indice pelo nome
                        if (currentIndexiesName.contains(compoundIndex.name())) {
                            //se chegou até aqui é sinal que existe,
                            //logo devemos verificar se a definição de ambos são equivalente
                            //caso contrario devemos atualizar o indice
                            if (!this.isEqualDefinitionIndex(
                                    BasicDBObject.parse(compoundIndex.def()),
                                    currentIdexies.parallelStream()
                                            .filter(it -> it.get("name").equals(compoundIndex.name()))
                                            .map(it -> (DBObject) it.get("key"))
                                            .findFirst()
                                            .orElse(null)
                            )) {
                                //se chegou até aqui logo o indice existe no banco, porem com parametros diferente
                                // por conta disso devemos dropar esse indice e criar um novo
                                this.dropIndex(collection, compoundIndex.name());
                                this.createIndex(collection, compoundIndex);
                            } else {
                                log.info("The index \"" + compoundIndex.name() + "\" already exists for collection \"" + COLLECTION + "\"");
                            }
                        } else {
                            //se chegou até aqui é sinal que não existe um indice com o mesmo nome,
                            //logo devemos verificar se exite algum indice com nome diferente, porem com a definição igual
                            //se existir definição igual, logo devemos renomear o mesmo
                            //se não existir devemos criar o mesmo
                            final DBObject existingItem = currentIdexies
                                    .parallelStream()
                                    //.map(it -> (DBObject) it.get("key"))
                                    .filter(it -> this.isEqualDefinitionIndex(BasicDBObject.parse(compoundIndex.def()), (DBObject) it.get("key")))
                                    .findFirst()
                                    .orElse(null);

                            if (existingItem != null) {
                                this.dropIndex(collection, existingItem.get("name").toString());
                            }
                            this.createIndex(collection, compoundIndex);
                        }

                    });

            //dropando indices que exista somente no banco de dados
            //pegando todos os indices
            final List<DBObject> currentIndexies = collection.getIndexInfo();
            //interando todos os indices e pegando somente os que não deveria existir
            currentIndexies.parallelStream()
                    .map(it -> it.get("name").toString())
                    .filter(name -> !name.equals("_id_"))
                    .filter(name -> compoundIndexesValues.get()
                            .parallel()
                            .map(CompoundIndex::name)
                            .filter(it -> it.equals(name))
                            .count() == 0
                    ).forEach(name -> {
                this.dropIndex(collection, name);
            });
        }
    }

    /**
     * Checa se uma nova definição de um indice é equivalente a um indice já existente
     */
    private boolean isEqualDefinitionIndex(final DBObject newIndex, final DBObject currentIndex) {
        if ((newIndex != null && currentIndex == null) || (newIndex == null && currentIndex != null)) {
            return false;
        }
        final List<String> keySetNewIndex = new LinkedList<>(newIndex.keySet());
        final List<String> keySetCurrentIndex = new LinkedList<>(currentIndex.keySet());
        if (keySetNewIndex.size() != keySetCurrentIndex.size()) {
            return false;
        }
        for (int i = 0; i < keySetNewIndex.size(); i++) {
            if ((!keySetNewIndex.get(i).equals(keySetCurrentIndex.get(i))) ||
                    (!(Double.valueOf(newIndex.get(keySetNewIndex.get(i)).toString()).intValue() == Double.valueOf(currentIndex.get(keySetCurrentIndex.get(i)).toString()).intValue()))) {
                return false;
            }
        }
        return true;
    }

    private void dropIndex(final DBCollection collection, final String name) {
        if (!"_id_".equals(name)) {
            collection.dropIndexes(name);
            log.info("The index \"" + name + "\" has been removed from collection \"" + COLLECTION + "\"");
        }
    }

    private void createIndex(final DBCollection collection, final CompoundIndex compoundIndex) {
        final DBObject indexDefinition = BasicDBObject.parse(compoundIndex.def());
        final DBObject options = new BasicDBObject();

        if (compoundIndex.background()) {
            options.put("background", 1);
        }

        if (compoundIndex.unique()) {
            options.put("unique", 1);
        }

        if (!StringUtils.isEmpty(compoundIndex.name())) {
            options.put("name", compoundIndex.name());
        }

        if (compoundIndex.sparse()) {
            options.put("sparse", compoundIndex.sparse());
        }

        collection.createIndex(indexDefinition, options);

        log.info("Created index \"" + compoundIndex.name() + "\" for collection \"" + COLLECTION + "\"");
    }


}
