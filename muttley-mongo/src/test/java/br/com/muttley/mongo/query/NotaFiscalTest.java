package br.com.muttley.mongo.query;
import br.com.muttley.mongo.infra.test.projections.Projection;
import org.junit.Test;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.DEFAULT_CONTEXT;

/**
 * @author Joel Rodrigues Moreira on 20/10/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class NotaFiscalTest {
    @Test
    public void matchByIDTest() {

        //final Projection projection = from(of(NotaFiscal.class), getQueryParams("www.asdf.com?id.$is=" + new ObjectId(new Date())));
        //print(projection);
    }

    private void print(final Projection projection) {
        final List<AggregationOperation> operations = projection.getPipeline();
        operations.forEach(it -> {
            it.toPipelineStages(DEFAULT_CONTEXT).forEach(iit -> {
                System.out.println(iit.toJson());
            });
        });
    }
}
