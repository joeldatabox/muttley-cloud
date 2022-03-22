package br.com.muttley.mongo.service.infra.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

/**
 * @author Joel Rodrigues Moreira on 22/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MapBuilder {
    public static AggregationExpression map(final String input, final String as, final AggregationExpression in) {
        return new AggregationExpression() {
            @Override
            public DBObject toDbObject(AggregationOperationContext context) {
                return new BasicDBObject("$map",
                        new BasicDBObject("input", input)
                                .append("as", as)
                                .append("in", in.toDbObject(context))
                );
            }
        };
    }
}
