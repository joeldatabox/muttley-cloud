package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.mongo.infra.newagregation.operators.Operator2;
import br.com.muttley.mongo.infra.newagregation.operators.Operator3;
import br.com.muttley.mongo.infra.newagregation.paramvalue.NewQueryParam;
import br.com.muttley.mongo.infra.newagregation.projections.Criterion3;
import br.com.muttley.mongo.infra.newagregation.projections.Criterion3Impl;
import br.com.muttley.mongo.infra.newagregation.projections.ProjectionMetadata;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static br.com.muttley.mongo.infra.newagregation.projections.Criterion3.CriterionBuilder.from;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.isEmpty;

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
    public boolean isTypeArray() {
        return false;
    }


}
