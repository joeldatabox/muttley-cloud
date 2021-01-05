package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.newagregation.operators.Operator2;
import br.com.muttley.mongo.infra.newagregation.projections.Projection2;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@EqualsAndHashCode(of = "wildcard")
public class OperatorCriteriaORDER_BY_DESC2 implements Operator2 {
    public static final String wildcard = "$orderByDesc";

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
                sort(DESC, (String[]) value)
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

}
