package br.com.muttley.mongo.infra.aggregations;

import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

/**
 * @author Joel Rodrigues Moreira on 12/10/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MuttleyProjectionOperation implements AggregationOperation {
    private final Document document;

    public static MuttleyProjectionOperation project(final Document document) {
        return new MuttleyProjectionOperation(document);
    }

    public static MuttleyProjectionOperation project(final BasicDBObject object) {
        return new MuttleyProjectionOperation(object);
    }

    protected MuttleyProjectionOperation(Document document) {
        this.document = document;
    }

    protected MuttleyProjectionOperation(final BasicDBObject object) {
        this(new Document(object));
    }

    @Override
    public Document toDocument(AggregationOperationContext context) {
        return new Document(getOperator(), this.document);
    }

    @Override
    public String getOperator() {
        return "$project";
    }
}
