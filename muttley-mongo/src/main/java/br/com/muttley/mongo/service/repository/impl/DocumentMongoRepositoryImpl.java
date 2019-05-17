package br.com.muttley.mongo.service.repository.impl;

import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryIdIsNullException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryInvalidIdException;
import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.muttley.mongo.service.infra.Aggregate.createAggregations;
import static br.com.muttley.mongo.service.infra.Aggregate.createAggregationsCount;
import static org.bson.types.ObjectId.isValid;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class DocumentMongoRepositoryImpl<T extends Document> extends SimpleMongoRepository<T, String> implements br.com.muttley.mongo.service.repository.DocumentMongoRepository<T> {
    protected final MongoOperations operations;
    protected final Class<T> CLASS;
    protected final String COLLECTION;

    public DocumentMongoRepositoryImpl(final MongoEntityInformation<T, String> metadata, final MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.operations = mongoOperations;
        this.CLASS = metadata.getJavaType();
        this.COLLECTION = metadata.getCollectionName();
    }

    @Override
    public T findFirst() {
        return operations.findOne(new Query(), CLASS);
    }

    @Override
    public List<T> findAll(final Map<String, Object> queryParams) {
        return operations
                .aggregate(
                        newAggregation(
                                createAggregations(CLASS,
                                        ((queryParams != null && !queryParams.isEmpty()) ? queryParams : new HashMap<>())
                                )
                        ),
                        COLLECTION, CLASS
                )
                .getMappedResults();
    }

    @Override
    public long count(final Map<String, Object> queryParams) {
        final AggregationResults result = operations.aggregate(
                newAggregation(
                        createAggregationsCount(
                                CLASS,
                                ((queryParams != null && !queryParams.isEmpty()) ? queryParams : new HashMap<>()))
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
}