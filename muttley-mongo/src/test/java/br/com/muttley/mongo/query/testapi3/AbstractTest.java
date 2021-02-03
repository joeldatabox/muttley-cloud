package br.com.muttley.mongo.query.testapi3;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.newagregation.paramvalue.QueryParam;
import br.com.muttley.mongo.infra.newagregation.projections.Projection;
import br.com.muttley.mongo.query.model2.NotaFiscal;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.Collection;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.DEFAULT_CONTEXT;

/**
 * @author Joel Rodrigues Moreira on 12/01/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class AbstractTest {
    protected final String URL_TEST;

    public AbstractTest(String url_test) {
        URL_TEST = url_test;
    }

    protected void printResult(final Collection<AggregationOperation> aggregations) {
        aggregations.forEach(it -> {
            it.toPipelineStages(DEFAULT_CONTEXT).forEach(iit -> {
                System.out.println(iit.toJson());
            });
        });
    }

    protected Projection getProjection(final Class clazz) {
        return Projection.Builder
                .newInstance()
                .withEntityMetadata(EntityMetaData.of(clazz))
                .withQueriesParams(
                        QueryParam.BuilderFromURL
                                .newInstance()
                                .fromURL(URL_TEST)
                                .build()
                ).build();
    }

    protected Projection getProjection() {
        return this.getProjection(NotaFiscal.class);
    }

    protected void printResult(Projection projection) {
        this.printResult(projection.getQuery());
    }
}
