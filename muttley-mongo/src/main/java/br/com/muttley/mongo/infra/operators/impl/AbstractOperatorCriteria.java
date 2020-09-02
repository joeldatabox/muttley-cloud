package br.com.muttley.mongo.infra.operators.impl;

import org.springframework.data.mongodb.core.query.Criteria;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractOperatorCriteria<T extends Criteria> extends AbstractOperator<T> {

    @Override
    public boolean isCriteriaOperation() {
        return true;
    }

    @Override
    public boolean isAggregationOperation() {
        return false;
    }
}
