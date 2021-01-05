package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.mongo.infra.newagregation.projections.ProjectionMetadata;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OperatorCriteriaLIMIT3 extends AbstractOperatorImpl {
    public static final String wildcard = "$limit";

    public OperatorCriteriaLIMIT3() {
        super(wildcard);
    }

    @Override
    public List<AggregationOperation> extractAggregations(ProjectionMetadata metadata, String compositePropertyWithFather, String key, Object value) {
        return new LinkedList<>(asList(
                limit(Long.valueOf(value.toString()))
        ));
    }

}
