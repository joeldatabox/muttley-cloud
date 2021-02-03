package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.mongo.infra.newagregation.projections.ProjectionMetadata;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Stream.of;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OperatorCriteriaIN extends OperatorCriteriaWithArray {
    public static final String wildcard = ".$in";

    public OperatorCriteriaIN() {
        super(wildcard);
    }

    @Override
    public List<Criteria> extractCriteria(final ProjectionMetadata metadata, final String compositePropertyWithFather, final String key, final Object value) {
        return new LinkedList<>(asList(
                new Criteria(compositePropertyWithFather).in(
                        of(this.splitArray(value.toString()))
                                .map(it -> metadata.converteValueFor(key, it))
                                .toArray()
                )
        ));
    }
}
