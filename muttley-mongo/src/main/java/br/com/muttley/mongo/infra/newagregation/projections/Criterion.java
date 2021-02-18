package br.com.muttley.mongo.infra.newagregation.projections;

import br.com.muttley.mongo.infra.newagregation.operators.Operator;
import br.com.muttley.mongo.infra.newagregation.paramvalue.QueryParam;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 10/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Criterion {
    Operator getOperator();

    String getKey();

    Object getValue();

    List<Criterion> getSubcriterions();

    List<AggregationOperation> extractAgregations();

    List<Criteria> extractCriteria();

    public static class CriterionBuilder {

        public static Criterion from(final ProjectionMetadata metadata, final QueryParam param) {
            final Operator operator = Operator.from(param.getKey());
            final CriterionImpl result;
            if (operator.isTypeArray()) {
                result = new CriterionImpl(metadata, operator, null, null).addSubcriterionsArray(param.getValue());//precisa extrair os subitens aqui
            } else {
                result = new CriterionImpl(metadata, operator, param.getKey(), param.getValue());
            }
            return result;
        }
    }
}
