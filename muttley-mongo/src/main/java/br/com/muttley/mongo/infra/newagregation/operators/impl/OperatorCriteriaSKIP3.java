package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.mongo.infra.newagregation.projections.ProjectionMetadata;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

public class OperatorCriteriaSKIP3 extends AbstractOperatorImpl {
    public static final String wildcard = "$skip";

    public OperatorCriteriaSKIP3() {
        super(wildcard);
    }

    @Override
    public List<AggregationOperation> extractAggregations(final ProjectionMetadata metadata, final String compositePropertyWithFather, final String key, final Object value) {
        return new LinkedList<>(asList(
                skip(Long.valueOf(value.toString()))
        ));
    }

}
