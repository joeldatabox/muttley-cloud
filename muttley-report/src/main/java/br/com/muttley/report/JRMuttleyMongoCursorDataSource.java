package br.com.muttley.report;

import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.report.strategy.MuttleyReportAggregationStrategy;
import com.mongodb.AggregationOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBObject;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static br.com.muttley.utils.MapUtils.getValueByNavigation;

/**
 * @author Joel Rodrigues Moreira on 03/09/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class JRMuttleyMongoCursorDataSource implements JRDataSource {
    protected final MongoTemplate mongoTemplate;

    protected final MuttleyReportAggregationStrategy aggregationStrategy;

    protected final String COLLECTION_NAME;
    protected Cursor cursor;
    protected BasicDBObject currentValue;
    protected long currentSkip = 0l;
    protected final Long currentLimit;
    protected boolean throwsExceptionsIsEmpty = true;


    public JRMuttleyMongoCursorDataSource(final MongoTemplate mongoTemplate, final MuttleyReportAggregationStrategy aggregationStrategy, final String collection) {
        this(mongoTemplate, aggregationStrategy, 100000l, collection);
    }

    public JRMuttleyMongoCursorDataSource(final MongoTemplate mongoTemplate, final MuttleyReportAggregationStrategy aggregationStrategy, final long limit, final String collection) {
        this.currentLimit = limit;
        this.currentSkip = limit * -1;
        this.mongoTemplate = mongoTemplate;
        this.aggregationStrategy = aggregationStrategy;
        this.COLLECTION_NAME = collection;
    }

    public JRMuttleyMongoCursorDataSource throwsExceptionsIsEmpty(final boolean throwsExceptionsIsEmpty) {
        this.throwsExceptionsIsEmpty = throwsExceptionsIsEmpty;
        return this;
    }
    protected boolean fetchQuery() {
        this.currentSkip += this.currentLimit;
        this.cursor = this.mongoTemplate.getCollection(this.COLLECTION_NAME)
                .aggregate(
                        this.createAggregationReport(this.currentSkip, this.currentLimit),
                        AggregationOptions.builder()
                                .batchSize(100)
                                .maxTime(60, TimeUnit.MINUTES)
                                .outputMode(AggregationOptions.OutputMode.CURSOR)
                                .allowDiskUse(true)
                                .build()
                );

        return cursor != null && cursor.hasNext();
    }

    @Override
    public boolean next() throws JRException {
        final boolean result;
        if (this.cursor == null) {
            //se é null quer dizer que estamos na primeira pagina
            result = this.fetchQuery();
            if (!cursor.hasNext() && this.throwsExceptionsIsEmpty) {
                //matando o cursor
                this.cursor.close();
                throw new MuttleyNoContentException(null, null, "Nenhum registro encontrado para o relatório!");
            }
        } else {
            result = this.cursor.hasNext() ? true : this.fetchQuery();
        }
        if (result) {
            this.currentValue = (BasicDBObject) this.cursor.next();
        } else {
            this.cursor.close();
        }
        return result;
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        return getValueByNavigation(jrField.getName(), this.currentValue);
    }

    protected List<DBObject> createAggregationReport(final long skip, final long limit) {
        return this.aggregationStrategy.getAggregation(skip, limit)
                .stream()
                .map(it -> it.toDBObject(Aggregation.DEFAULT_CONTEXT))
                .collect(Collectors.toList());
    }
}
