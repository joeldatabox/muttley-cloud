package br.com.muttley.mongo.query.projections;

import br.com.muttley.mongo.infra.operators.Operator;
import br.com.muttley.mongo.query.projections.Criterion;
import lombok.Getter;

/**
 * @author Joel Rodrigues Moreira on 02/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
public class CriterionImpl implements Criterion {
    private final Operator operator;
    private final Object value;

    protected CriterionImpl(Operator operator, Object value) {
        this.operator = operator;
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" + getOperator() + " : " + getValue() + "}";
    }
}
