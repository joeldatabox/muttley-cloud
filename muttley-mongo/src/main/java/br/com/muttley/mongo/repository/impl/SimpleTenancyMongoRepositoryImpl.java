package br.com.muttley.mongo.repository.impl;

import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryIdIsNullException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryInvalidIdException;
import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.util.StreamUtil;
import br.com.muttley.mongo.infra.AggregationUtils;
import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.repository.SimpleTenancyMongoRepository;
import br.com.muttley.mongo.service.annotations.CompoundIndexes;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static br.com.muttley.mongo.infra.metadata.EntityMetaData.of;
import static java.util.stream.Stream.of;
import static org.bson.types.ObjectId.isValid;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class SimpleTenancyMongoRepositoryImpl<T extends Document> extends SimpleMongoRepository<T, String> implements SimpleTenancyMongoRepository<T> {
    protected final MongoOperations operations;
    protected final Class<T> CLASS;
    protected final String COLLECTION;
    protected final EntityMetaData entityMetaData;
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    public SimpleTenancyMongoRepositoryImpl(@Autowired final MongoEntityInformation<T, String> metadata, @Autowired final MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.operations = mongoOperations;
        this.CLASS = metadata.getJavaType();
        this.COLLECTION = metadata.getCollectionName();
        this.entityMetaData = of(metadata.getJavaType());
        this.createIndexes(metadata);
    }

    @Override
    public boolean isEmpty() {
        return this.count((Map<String, String>) null) == 0l;
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
                                        ((queryParams != null && !queryParams.isEmpty()) ? queryParams : new HashMap<>())
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
        return this.exists("_id", value.getId());
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

    private void createIndexes(final MongoEntityInformation<T, String> metadata) {
        final CompoundIndexes compoundIndexes = metadata.getJavaType().getAnnotation(CompoundIndexes.class);
        if (compoundIndexes != null) {
            final MongoCursor<String> indexiesIterator = this.operations.getCollection(metadata.getCollectionName())
                    .listIndexes()
                    .map(index -> index.get("name"))
                    .map(Object::toString)
                    .iterator();
            final List<String> indexies = new ArrayList<>();

            //pegando os nomes dos indices
            indexiesIterator.forEachRemaining(indexies::add);

            for (final CompoundIndex compoundIndex : compoundIndexes.value()) {

                if (!indexies.contains(compoundIndex.name())) {

                    final BasicDBObject indexDefinition = BasicDBObject.parse(compoundIndex.def());
                    final IndexOptions options = new IndexOptions();

                    options.background(compoundIndex.background());
                    options.unique(compoundIndex.unique());
                    options.name(compoundIndex.name());
                    options.sparse(compoundIndex.sparse());

                    this.operations.getCollection(metadata.getCollectionName())
                            .createIndex(indexDefinition, options);

                    log.info("Created index \"" + compoundIndex.name() + "\" for collection \"" + COLLECTION + "\"");
                } else {
                    log.info("The index \"" + compoundIndex.name() + "\" already exists for collection \"" + COLLECTION + "\"");
                }
            }
        }
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

        public boolean isEmpty() {
            return count == 0L;
        }
    }
}
