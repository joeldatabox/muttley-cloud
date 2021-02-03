package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.mongo.infra.newagregation.projections.ProjectionMetadata;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@EqualsAndHashCode(of = "wildcard")
public class OperatorCriteriaGT extends AbstractOperatorImpl {
    public static final String wildcard = ".$gt";

    public OperatorCriteriaGT() {
        super(wildcard);
    }

    @Override
    public List<Criteria> extractCriteria(final ProjectionMetadata metadata, final String compositePropertyWithFather, final String key, final Object value) {
        return new LinkedList<>(asList(
                new Criteria(compositePropertyWithFather).gt(metadata.converteValueFor(key, value))
        ));
    }
}
