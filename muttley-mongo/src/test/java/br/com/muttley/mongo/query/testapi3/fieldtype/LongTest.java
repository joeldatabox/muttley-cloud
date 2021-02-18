package br.com.muttley.mongo.query.testapi3.fieldtype;

import br.com.muttley.mongo.query.model2.NotaFiscal;
import br.com.muttley.mongo.query.testapi3.AbstractTest;
import org.junit.Test;

/**
 * @author Joel Rodrigues Moreira on 12/01/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class LongTest extends AbstractTest {
    public LongTest() {
        super("www.test.com.br?empresa.dtSync.$lte=2020-06-11T08:53:41.988-0300&itens.produto.descricao=asdfasd&itens.qtde.$lt=965.90&sequencial=1950");
    }

    @Test
    public void testValidLong() {
        this.printResult(getProjection(NotaFiscal.class));
    }
}
