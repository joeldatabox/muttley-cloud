package br.com.muttley.mongo.service.infra.util;

import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.util.Collection;

/**
 * @author Joel Rodrigues Moreira on 09/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ListReduceBuilder {
    public static ArrayOperators.Reduce reduce(final String array, final Collection initialValue, final ArrayOperators.Reduce.PropertyExpression... expressions) {
        return reduce(array).withInitialValue(initialValue).reduce(expressions);
    }

    public static ArrayOperators.Reduce reduce(final String array, final Collection initialValue, final AggregationExpression expression) {
        return reduce(array).withInitialValue(initialValue).reduce(expression);
    }

    public static ArrayOperators.Reduce.InitialValueBuilder reduce(final String array) {
        return ArrayOperators.Reduce.arrayOf(array);
    }
}
