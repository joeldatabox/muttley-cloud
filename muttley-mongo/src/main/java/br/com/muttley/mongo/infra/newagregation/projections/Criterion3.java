package br.com.muttley.mongo.infra.newagregation.projections;

import br.com.muttley.mongo.infra.newagregation.operators.Operator2;
import br.com.muttley.mongo.infra.newagregation.paramvalue.NewQueryParam;

import java.util.List;

import static br.com.muttley.mongo.infra.newagregation.operators.Operator2.OR;

/**
 * @author Joel Rodrigues Moreira on 10/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Criterion3 {
    Operator2 getOperator();

    String getKey();

    Object getValue();

    List<Criterion3> getSubcriterions();

    public static class CriterionBuilder {

        public static Criterion3 from(final ProjectionMetadata metadata, final NewQueryParam param) {
            final Operator2 operator2 = Operator2.of(param.getKey());
            final Criterion3Impl result;
            if (OR.equals(operator2)) {
                result = new Criterion3Impl(metadata, operator2, null, null, null);//precisa extrair os subitens aqui
            } else {
                //result = new Criterion3Impl(operator2, param.getKey(), param.getValue(), null);
                result = null;
            }
            return result;
        }
    }
}
