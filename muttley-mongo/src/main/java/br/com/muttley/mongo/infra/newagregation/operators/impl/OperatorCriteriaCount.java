package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.mongo.infra.newagregation.projections.ProjectionMetadata;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.count;

/**
 * @author Joel Rodrigues Moreira on 02/02/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

public class OperatorCriteriaCount extends AbstractOperatorImpl {
    public static final String wildcard = "$count";

    public OperatorCriteriaCount() {
        super(wildcard);
    }

    @Override
    public List<AggregationOperation> extractAggregations(final ProjectionMetadata metadata, final String compositePropertyWithFather, final String key, final Object value) {
        return new LinkedList<>(asList(
                count().as("count")
        ));
    }

}
