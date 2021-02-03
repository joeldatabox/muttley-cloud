package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.mongo.infra.newagregation.projections.Criterion;
import br.com.muttley.mongo.infra.newagregation.projections.ProjectionMetadata;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OperatorCriteriaOR extends AbstractOperatorImpl {
    public static final String wildcard = ".$or";

    public OperatorCriteriaOR() {
        super(wildcard);
    }

    @Override
    public List<Criteria> extractCriteriaArray(ProjectionMetadata metadata, List<Criterion> subcriterions) {
        return new LinkedList<>(
                asList(new Criteria()
                        .orOperator(
                                subcriterions
                                        .stream()
                                        .map(it -> it.extractCriteria())
                                        .flatMap(Collection::stream)
                                        .toArray(Criteria[]::new)
                        )
                )
        );
    }

    @Override
    public boolean isTypeArray() {
        return true;
    }
}
