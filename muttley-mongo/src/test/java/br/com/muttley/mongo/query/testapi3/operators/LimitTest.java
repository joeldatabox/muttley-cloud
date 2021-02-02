package br.com.muttley.mongo.query.testapi3.operators;

import br.com.muttley.mongo.query.model2.NotaFiscal;
import br.com.muttley.mongo.query.testapi3.AbstractTest;
import org.junit.Test;

/**
 * @author Joel Rodrigues Moreira on 01/02/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class LimitTest extends AbstractTest {
    public LimitTest() {
        super("www.test.com.br?empresa.dtSync.$lt=2020-06-11T08:53:41.988-0300&sequencial.$lt=697&$limit=10");
    }

    @Test
    public void testLimiteOperator() {
        this.printResult(getProjection(NotaFiscal.class));
    }
}
