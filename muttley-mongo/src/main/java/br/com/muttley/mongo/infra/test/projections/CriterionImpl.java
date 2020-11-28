package br.com.muttley.mongo.infra.test.projections;

import br.com.muttley.mongo.infra.operators.Operator;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Joel Rodrigues Moreira on 02/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
public class CriterionImpl implements Criterion {
    @Setter
    @Accessors(chain = true)
    private int order;
    @Setter
    @Accessors(chain = true)
    private int level;
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
