package br.com.muttley.mongo.query.testapi3.operators;

import br.com.muttley.mongo.query.model2.NotaFiscal;
import br.com.muttley.mongo.query.testapi3.AbstractTest;
import org.junit.Test;

/**
 * @author Joel Rodrigues Moreira on 02/02/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ContainsTest extends AbstractTest {
    public ContainsTest() {
        super("www.test.com.br?empresa.fantasia.$contains=testando&sequencial.$gte=697");
    }

    @Test
    public void testContainsOperator() {
        this.printResult(getProjection(NotaFiscal.class));
    }
}
