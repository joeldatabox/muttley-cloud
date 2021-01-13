package br.com.muttley.mongo.query.testapi3;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.newagregation.projections.Projection3;
import br.com.muttley.mongo.query.URLParaTest;
import br.com.muttley.mongo.query.model2.NotaFiscal;
import org.junit.Test;

/**
 * @author Joel Rodrigues Moreira on 12/01/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class DBRefTest extends AbstractTest {

    public DBRefTest() {
        super("www.test.com.br?empresa.id=5e28c73d6f985c00017e7bd9");
    }

    @Test
    public void testValidId() {
        final Projection3 projection = Projection3.ProjectionBuilder.from(EntityMetaData.of(NotaFiscal.class), URLParaTest.getQueryParams(URL_TEST));

        this.printResult(projection);
    }
}
