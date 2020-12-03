package br.com.muttley.mongo.infra.operators.impl;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.operators.Operator2;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OperatorCriteriaOR2 implements Operator2 {
    private static final String wildcard = ".$or";

    @Override
    public String getWildcard() {
        return wildcard;
    }

    @Override
    public List<AggregationOperation> extractAggregations(EntityMetaData entityMetaData, String key, Object value) {
        return null;
    }

    @Override
    public List<AggregationOperation> extractAggregations(EntityMetaData entityMetaData, String compositePropertyWithFather, String key, Object value) {
        return null;
    }

    @Override
    public List<Criteria> extractCriteria(EntityMetaData entityMetaData, String key, Object value) {
        return null;
    }

    @Override
    public List<Criteria> extractCriteria(EntityMetaData entityMetaData, String compositePropertyWithFather, String key, Object value) {
        return null;
    }
}
