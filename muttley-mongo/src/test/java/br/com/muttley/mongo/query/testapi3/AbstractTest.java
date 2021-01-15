package br.com.muttley.mongo.query.testapi3;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.newagregation.projections.Projection3;
import br.com.muttley.mongo.query.URLParaTest;
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

    protected Projection3 getProjection(final Class clazz) {
        return Projection3.ProjectionBuilder.from(EntityMetaData.of(clazz), URLParaTest.getQueryParams(URL_TEST));
    }

    protected Projection3 getProjection() {
        return this.getProjection(NotaFiscal.class);
    }

    protected void printResult(Projection3 projection) {
        this.printResult(projection.getQuery());
    }
}
