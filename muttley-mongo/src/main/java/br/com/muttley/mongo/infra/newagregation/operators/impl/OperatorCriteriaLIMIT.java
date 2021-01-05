package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OperatorCriteriaLIMIT extends AbstractOperatorAggregationOperation {
    public static final String wildcard = "$limit";

    @Override
    public String getWildcard() {
        return wildcard;
    }

    @Override
    public AggregationOperation extract(final EntityMetaData entityMetaData, final String key, final Object value) {
        return this.extract(entityMetaData, key, key, value);
    }

    @Override
    public AggregationOperation extract(EntityMetaData entityMetaData, String compositePropertyWithFather, String key, Object value) {
        return limit(Long.valueOf(value.toString()));
    }
}
