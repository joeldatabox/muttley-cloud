package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.mongo.infra.newagregation.operators.Operator3;
import br.com.muttley.mongo.infra.newagregation.projections.Criterion3;
import br.com.muttley.mongo.infra.newagregation.projections.ProjectionMetadata;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 22/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@EqualsAndHashCode(of = "wildcard")
public abstract class AbstractOperatorImpl implements Operator3 {
    @Getter
    protected final String wildcard;

    public AbstractOperatorImpl(String wildcard) {
        this.wildcard = wildcard;
    }

    @Override
    public List<AggregationOperation> extractAggregations(final ProjectionMetadata metadata, final String key, final Object value) {
        return this.extractAggregations(metadata, key, key, value);
    }

    @Override
    public List<AggregationOperation> extractAggregations(final ProjectionMetadata metadata, final String compositePropertyWithFather, final String key, final Object value) {
        return new LinkedList();
    }

    @Override
    public List<Criteria> extractCriteria(final ProjectionMetadata metadata, final String key, final Object value) {
        return this.extractCriteria(metadata, key, key, value);
    }

    @Override
    public List<Criteria> extractCriteria(final ProjectionMetadata metadata, final String compositePropertyWithFather, final String key, final Object value) {
        return new LinkedList<>();
    }

    @Override
    public List<Criteria> extractCriteriaArray(final ProjectionMetadata metadata, final List<Criterion3> subcriterions) {
        return new LinkedList<>();
    }

    @Override
    public boolean isTypeArray() {
        return false;
    }

    @Override
    public String toString() {
        return wildcard;
    }
}
