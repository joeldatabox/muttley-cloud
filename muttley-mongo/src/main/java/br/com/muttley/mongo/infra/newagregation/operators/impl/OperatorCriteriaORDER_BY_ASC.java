package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OperatorCriteriaORDER_BY_ASC extends AbstractOperatorAggregationOperation {
    private static final String wildcard = "$orderByAsc";

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
        return sort(ASC, (String[]) value);
    }
}
