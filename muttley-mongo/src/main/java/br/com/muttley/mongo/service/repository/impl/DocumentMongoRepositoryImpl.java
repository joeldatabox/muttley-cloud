package br.com.muttley.mongo.service.repository.impl;

import br.com.muttley.exception.throwables.repository.MuttleyRepositoryIdIsNullException;
import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.muttley.mongo.service.infra.Aggregate.createAggregations;
import static br.com.muttley.mongo.service.infra.Aggregate.createAggregationsCount;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class DocumentMongoRepositoryImpl<T extends Document<ID>, ID extends ObjectId> extends SimpleMongoRepository<T, ID> implements br.com.muttley.mongo.service.repository.DocumentMongoRepository<T, ID> {
    protected final MongoOperations operations;
    protected final Class<T> CLASS;
    protected final String COLLECTION;

    public DocumentMongoRepositoryImpl(final MongoEntityInformation<T, ID> metadata, final MongoOperations mongoOperations) {
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
    public Historic loadHistoric(final T value) {
        return loadHistoric(value.getId());
    }

    @Override
    public Historic loadHistoric(final ID id) {
        final AggregationResults result = operations.aggregate(
                newAggregation(
                        match(
                                where("id").is(id)
                        ), project().and("historic").as("historic")
                ), COLLECTION, Historic.class);

        return result.getUniqueMappedResult() != null ? ((Historic) result.getUniqueMappedResult()) : null;
    }

    protected final void validateId(final ID id) {
        if (id == null) {
            throw new MuttleyRepositoryIdIsNullException(this.CLASS);
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
    }
}