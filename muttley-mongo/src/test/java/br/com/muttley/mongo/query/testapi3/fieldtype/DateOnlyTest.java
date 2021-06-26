package br.com.muttley.mongo.query.testapi3.fieldtype;

import br.com.muttley.mongo.query.model2.NotaFiscal;
import br.com.muttley.mongo.query.testapi3.AbstractTest;
import org.junit.Test;

/**
 * @author Joel Rodrigues Moreira on 12/01/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class DateOnlyTest extends AbstractTest {
    public DateOnlyTest() {
        super("www.test.com.br?empresa.id=5e28c73d6f985c00017e7bd9&empresa.dtSync.$gte=2020-10-10");
    }

    @Test
    public void testValidDate() {
        this.printResult(getProjection(NotaFiscal.class));
    }
}