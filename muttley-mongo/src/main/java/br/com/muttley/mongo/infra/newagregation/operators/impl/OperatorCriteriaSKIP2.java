package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.newagregation.operators.Operator2;
import br.com.muttley.mongo.infra.newagregation.projections.Projection2;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OperatorCriteriaSKIP2 implements Operator2 {
    private static final String wildcard = "$skip";

    @Override
    public String getWildcard() {
        return wildcard;
    }

    @Override
    public List<AggregationOperation> extractAggregations(final Projection2 projection, final EntityMetaData entityMetaData, final String key, final Object value) {
        return this.extractAggregations(projection, entityMetaData, key, key, value);
    }

    @Override
    public List<AggregationOperation> extractAggregations(final Projection2 projection, final EntityMetaData entityMetaData, final String compositePropertyWithFather, final String key, final Object value) {
        return new LinkedList<>(asList(
                skip(Long.valueOf(value.toString()))
        ));
    }

    @Override
    public List<Criteria> extractCriteria(final Projection2 projection, final EntityMetaData entityMetaData, final String key, final Object value) {
        return this.extractCriteria(projection, entityMetaData, key, key, value);
    }

    @Override
    public List<Criteria> extractCriteria(final Projection2 projection, final EntityMetaData entityMetaData, final String compositePropertyWithFather, final String key, final Object value) {
        return new LinkedList<>();
    }

    /*@Override
    public List<AggregationOperation> extract(final EntityMetaData entityMetaData, final String key, final Object value) {
        return this.extract(entityMetaData, key, key, value);
    }

    @Override
    public List<AggregationOperation> extract(EntityMetaData entityMetaData, String compositePropertyWithFather, String key, Object value) {
        return skip(Long.valueOf(value.toString()));
    }*/
}
