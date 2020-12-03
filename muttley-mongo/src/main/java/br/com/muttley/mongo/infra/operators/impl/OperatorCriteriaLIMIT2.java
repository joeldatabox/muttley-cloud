package br.com.muttley.mongo.infra.operators.impl;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.operators.Operator2;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OperatorCriteriaLIMIT2 implements Operator2 {
    private static final String wildcard = "$limit";

    @Override
    public String getWildcard() {
        return wildcard;
    }

    @Override
    public List<AggregationOperation> extractAggregations(EntityMetaData entityMetaData, String key, Object value) {
        return this.extractAggregations(entityMetaData, key, key, value);
    }

    @Override
    public List<AggregationOperation> extractAggregations(EntityMetaData entityMetaData, String compositePropertyWithFather, String key, Object value) {
        return new LinkedList<>(asList(
                limit(Long.valueOf(value.toString()))
        ));
    }

    @Override
    public List<Criteria> extractCriteria(EntityMetaData entityMetaData, String key, Object value) {
        return this.extractCriteria(entityMetaData, key, key, value);
    }

    @Override
    public List<Criteria> extractCriteria(EntityMetaData entityMetaData, String compositePropertyWithFather, String key, Object value) {
        return new LinkedList<>();
    }

}
