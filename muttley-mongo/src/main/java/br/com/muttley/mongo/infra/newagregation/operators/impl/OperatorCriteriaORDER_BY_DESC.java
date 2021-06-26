package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.mongo.infra.newagregation.projections.ProjectionMetadata;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

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
public class OperatorCriteriaORDER_BY_DESC extends OperatorCriteriaWithArray {
    public static final String wildcard = "$orderByDesc";

    public OperatorCriteriaORDER_BY_DESC() {
        super(wildcard);
    }

    @Override
    public List<AggregationOperation> extractAggregations(final ProjectionMetadata metadata, final String compositePropertyWithFather, final String key, final Object value) {
        return new LinkedList<>(asList(
                sort(DESC, this.splitArray(value.toString()))
        ));
    }

}