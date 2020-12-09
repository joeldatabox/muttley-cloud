package br.com.muttley.mongo.infra.newagregation.projections;

import br.com.muttley.mongo.infra.newagregation.operators.Operator2;
import br.com.muttley.mongo.infra.newagregation.paramvalue.NewQueryParam;

/**
 * @author Joel Rodrigues Moreira on 02/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Criterion2 {
    Operator2 getOperator();

    Object getValue();

    public static class Criterion2Builder {
        private Criterion2Builder() {
        }

        public static Criterion2 from(final NewQueryParam param) {
            final Criterion2Impl result = new Criterion2Impl(Operator2.of(param.getKey()), param.getValue());
            result.setKey(param.getKey());
            return result;
        }
    }
}
