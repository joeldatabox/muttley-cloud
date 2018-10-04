package br.com.muttley.mongo.repository.impl;

import br.com.muttley.exception.throwables.repository.MuttleyRepositoryOwnerNotInformedException;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MultiTenancyModel;
import br.com.muttley.model.security.Owner;
import br.com.muttley.mongo.infra.Aggregate;
import br.com.muttley.mongo.repository.MultiTenancyMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class MultiTenancyMongoRepositoryImpl<T extends MultiTenancyModel> extends SimpleTenancyMongoRepositoryImpl<T> implements MultiTenancyMongoRepository<T> {

    public MultiTenancyMongoRepositoryImpl(@Autowired final MongoEntityInformation<T, String> metadata, @Autowired final MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
    }

    @Override
    public boolean isEmpty(final Owner owner) {
        validateOwner(owner);
        final AggregationResults result = operations.aggregate(
                Aggregation.newAggregation(
                        Aggregate.createAggregationsCount(
                                CLASS,
                                new HashMap(addOwnerQueryParam(owner, new HashMap()))
                        )),
                COLLECTION, ResultCount.class);
        return result.getUniqueMappedResult() != null ? !(((ResultCount) result.getUniqueMappedResult()).getCount() > 0) : false;
    }

    @Override
    public final T save(final Owner owner, final T value) {
        validateOwner(owner);
        value.setOwner(owner);
        return super.save(value);
    }

    @Override
    public final T findOne(final Owner owner, final String id) {
        validateOwner(owner);
        return operations.findOne(
                new Query(
                        where("owner.$id").is(owner.getObjectId())
                                .and("id").is(newObjectId(id))
                ), CLASS
        );
    }

    @Override
    public T findFirst(final Owner owner) {
        validateOwner(owner);
        return operations
                .findOne(
                        new Query(
                                where("owner.$id").is(owner.getObjectId())
                        ), CLASS
                );
    }

    @Override
    public final void delete(final Owner owner, final String id) {
        validateOwner(owner);
        operations.remove(
                new Query(
                        where("owner.$id").is(owner.getObjectId())
                                .and("id").is(newObjectId(id))
                ), CLASS
        );
    }

    @Override
    public final void delete(final Owner owner, final T value) {
        validateOwner(owner);
        operations.remove(
                new Query(
                        where("owner.$id").is(owner.getObjectId())
                                .and("id").is(value.getObjectId())
                ), CLASS
        );

    }

    @Override

    public final List<T> findAll(final Owner owner, final Map<String, Object> queryParams) {
        validateOwner(owner);
        return operations.aggregate(
                Aggregation.newAggregation(
                        Aggregate.createAggregations(
                                CLASS,
                                new HashMap<>(addOwnerQueryParam(owner, ((queryParams != null && !queryParams.isEmpty()) ? queryParams : new HashMap<>())))
                        )
                ),
                COLLECTION, CLASS)
                .getMappedResults();
    }

    @Override
    public final long count(final Owner owner, final Map<String, Object> queryParams) {
        validateOwner(owner);
        final AggregationResults result = operations.aggregate(
                Aggregation.newAggregation(
                        Aggregate.createAggregationsCount(
                                CLASS,
                                new HashMap<>(addOwnerQueryParam(owner, ((queryParams != null && !queryParams.isEmpty()) ? queryParams : new HashMap<>())))
                        )),
                COLLECTION, ResultCount.class);
        return result.getUniqueMappedResult() != null ? ((ResultCount) result.getUniqueMappedResult()).getCount() : 0;
    }

    @Override
    public final boolean exists(final Owner owner, final T value) {
        return exists(owner, value.getId());
    }

    @Override
    public final boolean exists(final Owner owner, final String id) {
        validateOwner(owner);
        return operations.exists(
                new Query(
                        where("owner.$id").is(owner.getObjectId())
                                .and("id").is(newObjectId(id))
                ), CLASS
        );
    }

    @Override
    public boolean exists(final Owner owner, final Map<String, Object> filter) {
        filter.put("owner.$id", owner.getObjectId());
        return exists(filter);
    }

    @Override
    public boolean exists(final Owner owner, final Object... filter) {
        final Object filters[] = new Object[2 + filter.length];
        filters[0] = "owner.$id";
        filters[1] = owner.getObjectId();
        for (int i = 0; i < filter.length; i++) {
            filters[i + 2] = filter[i];
        }
        return exists(filters);
    }

    @Override
    public Historic loadHistoric(final Owner owner, final T value) {
        final AggregationResults result = operations.aggregate(
                newAggregation(
                        match(where("owner.$id").is(owner.getObjectId())
                                .and("_id").is(value.getObjectId())
                        ), project().and("$historic.createdBy").as("createdBy")
                                .and("$historic.dtCreate").as("dtCreate")
                                .and("$historic.dtChange").as("dtChange")
                ), COLLECTION, Historic.class);

        return result.getUniqueMappedResult() != null ? ((Historic) result.getUniqueMappedResult()) : null;
    }

    @Override
    public Historic loadHistoric(final Owner owner, final String id) {
        final AggregationResults result = operations.aggregate(
                newAggregation(
                        match(where("owner.$id").is(owner.getObjectId())
                                .and("_id").is(newObjectId(id))
                        ), project().and("$historic.createdBy").as("createdBy")
                                .and("$historic.dtCreate").as("dtCreate")
                                .and("$historic.dtChange").as("dtChange")
                ), COLLECTION, Historic.class);

        return result.getUniqueMappedResult() != null ? ((Historic) result.getUniqueMappedResult()) : null;
    }

    private final void validateOwner(final Owner owner) {
        if (owner == null) {
            throw new MuttleyRepositoryOwnerNotInformedException(this.CLASS);
        }
    }

    private final Map<String, Object> addOwnerQueryParam(final Owner owner, final Map<String, Object> queryParams) {
        final Map<String, Object> query = new HashMap<>(1);
        query.put("owner.$id.$is", owner.getObjectId());
        if (queryParams != null) {
            query.putAll(queryParams);
        }
        return query;
    }
}