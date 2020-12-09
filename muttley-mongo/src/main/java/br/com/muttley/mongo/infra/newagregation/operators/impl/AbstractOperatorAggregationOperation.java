package br.com.muttley.mongo.infra.newagregation.operators.impl;

import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractOperatorAggregationOperation<T extends AggregationOperation> extends AbstractOperator<T> {

    @Override
    public boolean isCriteriaOperation() {
        return false;
    }

    @Override
    public boolean isAggregationOperation() {
        return true;
    }
}
