package br.com.muttley.mongo.repository.impl;

import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryInvalidIdException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryOwnerNotInformedException;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.MultiTenancyModel;
import br.com.muttley.model.security.Owner;
import br.com.muttley.mongo.infra.newagregation.paramvalue.QueryParam;
import br.com.muttley.mongo.infra.newagregation.projections.Projection;
import br.com.muttley.mongo.repository.MultiTenancyMongoRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Stream.of;
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
        return count(owner, null) == 0l;
    }

    @Override
    public final T save(final Owner owner, final T value) {
        validateOwner(owner);
        value.setOwner(owner);
        return super.save(value);
    }

    @Override
    public final T findOne(final Owner owner, final String id) {
        try {
            validateOwner(owner);
            final ObjectId objectId = newObjectId(id);
            return operations.findOne(
                    new Query(
                            where("owner.$id").is(owner.getObjectId())
                                    .and("id").is(newObjectId(id))
                    ), CLASS
            );
        } catch (MuttleyRepositoryInvalidIdException ex) {
            throw new MuttleyNotFoundException(CLASS, "id", "Registro não encontrado");
        }
    }

    @Override
    public Set<T> findMulti(final Owner owner, final String[] ids) {

        validateOwner(owner);

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
                            where("owner.$id").is(owner.getObjectId())
                                    .and("id").in(objectIds)
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
        try {
            validateOwner(owner);
            operations.remove(
                    new Query(
                            where("owner.$id").is(owner.getObjectId())
                                    .and("id").is(newObjectId(id))
                    ), CLASS
            );
        } catch (MuttleyRepositoryInvalidIdException ex) {
            throw new MuttleyNotFoundException(CLASS, "id", "Registro não encontrado");
        }
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
    public final List<T> findAll(final Owner owner, final String urlRequest) {
        validateOwner(owner);

        return operations.aggregate(
                newAggregation(
                        Projection.Builder
                                .newInstance()
                                .withEntityMetadata(this.entityMetaData)
                                .withQueriesParams(
                                        QueryParam.BuilderFromURL
                                                .newInstance()
                                                .fromURL(urlRequest)
                                                .build()
                                ).addQueryParamFirst(
                                QueryParam.Builder
                                        .newInstance()
                                        .withKey("owner.$is")
                                        .withValue(owner.getObjectId().toString())
                                        .build())
                                .build()
                                .getQuery()
                ),
                COLLECTION, CLASS
        ).getMappedResults();

        /*return operations.aggregate(
                newAggregation(
                        AggregationUtils.createAggregations(this.entityMetaData, getBasicPipelines(this.CLASS),

                                new HashMap<>(addOwnerQueryParam(owner, ((queryParams != null && !queryParams.isEmpty()) ? queryParams : new HashMap<>())))
                        )
                ),
                COLLECTION, CLASS)
                .getMappedResults();*/
    }

    @Override
    public final long count(final Owner owner, final String urlRequest) {
        validateOwner(owner);

        final AggregationResults result = operations.aggregate(
                newAggregation(
                        Projection.Builder
                                .newInstance()
                                .withEntityMetadata(this.entityMetaData)
                                .withQueriesParams(
                                        QueryParam.BuilderFromURL
                                                .newInstance()
                                                .fromURL(urlRequest)
                                                .build()
                                )
                                .addQueryParamFirst(
                                        QueryParam.Builder
                                                .newInstance()
                                                .withKey("owner.$is")
                                                .withValue(owner.getObjectId().toString())
                                                .build()
                                ).addCountParamEndsIfNotExists()
                                .build()
                                .getQuery()
                ),
                COLLECTION, ResultCount.class
        );

        return result.getUniqueMappedResult() != null ? ((ResultCount) result.getUniqueMappedResult()).getCount() : 0;

    }

    @Override
    public final boolean exists(final Owner owner, final T value) {
        return exists(owner, value.getId());
    }

    @Override
    public final boolean exists(final Owner owner, final String id) {
        try {
            validateOwner(owner);
            return operations.exists(
                    new Query(
                            where("owner.$id").is(owner.getObjectId())
                                    .and("id").is(newObjectId(id))
                    ), CLASS
            );
        } catch (MuttleyRepositoryInvalidIdException ex) {
            throw new MuttleyNotFoundException(CLASS, "id", "Registro não encontrado");
        }
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
    public MetadataDocument loadMetadata(final Owner owner, final T value) {
        final AggregationResults result = operations.aggregate(
                newAggregation(
                        match(where("owner.$id").is(owner.getObjectId())
                                .and("_id").is(value.getObjectId())
                        ), project().and("$metaData.timeZones").as("timeZones")
                                .and("$metaData.versionDocument").as("versionDocument")
                ), COLLECTION, MetadataDocument.class);

        return result.getUniqueMappedResult() != null ? ((MetadataDocument) result.getUniqueMappedResult()) : null;
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
        try {
            final AggregationResults result = operations.aggregate(
                    newAggregation(
                            match(where("owner.$id").is(owner.getObjectId())
                                    .and("_id").is(newObjectId(id))
                            ), project().and("$historic.createdBy").as("createdBy")
                                    .and("$historic.dtCreate").as("dtCreate")
                                    .and("$historic.dtChange").as("dtChange")
                    ), COLLECTION, Historic.class);

            return result.getUniqueMappedResult() != null ? ((Historic) result.getUniqueMappedResult()) : null;
        } catch (MuttleyRepositoryInvalidIdException ex) {
            throw new MuttleyNotFoundException(CLASS, "id", "Registro não encontrado");
        }
    }

    @Override
    public MetadataDocument loadMetadata(final Owner owner, final String id) {
        final AggregationResults result = operations.aggregate(
                newAggregation(
                        match(where("owner.$id").is(owner.getObjectId())
                                .and("_id").is(newObjectId(id))
                        ), project().and("$metadata.timeZones").as("timeZones")
                                .and("$metadata.versionDocument").as("versionDocument")
                ), COLLECTION, MetadataDocument.class);

        return result.getUniqueMappedResult() != null ? ((MetadataDocument) result.getUniqueMappedResult()) : null;
    }

    private final void validateOwner(final Owner owner) {
        if (owner == null) {
            throw new MuttleyRepositoryOwnerNotInformedException(this.CLASS);
        }
    }

    private final Map<String, String> addOwnerQueryParam(final Owner owner, final Map<String, String> queryParams) {
        final Map<String, String> query = new HashMap<>(1);
        query.put("owner.$id.$is", owner.getObjectId().toString());
        if (queryParams != null) {
            query.putAll(queryParams);
        }
        return query;
    }
}
