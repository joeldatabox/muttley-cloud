package br.com.muttley.mongo.infra;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;

import java.util.Map;

import static br.com.muttley.mongo.infra.Operator.IN;
import static br.com.muttley.mongo.infra.Operator.LIMIT;
import static br.com.muttley.mongo.infra.Operator.OR;
import static br.com.muttley.mongo.infra.Operator.ORDER_BY_ASC;
import static br.com.muttley.mongo.infra.Operator.ORDER_BY_DESC;
import static br.com.muttley.mongo.infra.Operator.SKIP;

/**
 * @author Joel Rodrigues Moreira on 27/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class AggregationFactory {

    public static class AggregationOperationItem {
        private final long order;
        private String field;
        private Map<Operator, Object> map;

        public AggregationOperationItem(long order) {
            this.order = order;
        }

        public AggregationOperationItem add(final Operator operator, final String value) {
            switch (operator) {
                case CONTAINS:
                case GTE:
                case LT:
                case IS:
                case OR:
                case SKIP:
                case LIMIT:
                    this.map.put(operator, value);
                /*case IN:
                    addParamInTriMap(triMap, keyTrimap, IN, split(String.valueOf(value)));
                    break;
                case ORDER_BY_ASC:
                    addParamInTriMap(triMap, keyTrimap, ORDER_BY_ASC, split(String.valueOf(value)));
                    break;
                case ORDER_BY_DESC:
                    addParamInTriMap(triMap, keyTrimap, ORDER_BY_DESC, split(String.valueOf(value)));
                    break;*/
                default:
                    throw new MuttleyBadRequestException(null, null, "A requisição contem criterios inválidos");
            }
        }
    }
}
