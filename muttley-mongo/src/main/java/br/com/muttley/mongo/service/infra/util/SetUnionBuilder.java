package br.com.muttley.mongo.service.infra.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

/**
 * @author Joel Rodrigues Moreira on 09/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class SetUnionBuilder {
    public static AggregationExpression setUnion(final Object... arrays) {
        return new AggregationExpression() {

            @Override
            public DBObject toDbObject(AggregationOperationContext context) {
                return new BasicDBObject("$setUnion", arrays);
            }
        };
    }

    public static AggregationExpression setUnion(final String... arrays) {
        return setUnion((Object[]) arrays);
    }
}
