package br.com.muttley.mongo.query.testapi3.fieldtype;

import br.com.muttley.mongo.query.model2.NotaFiscal;
import br.com.muttley.mongo.query.testapi3.AbstractTest;
import org.junit.Test;

/**
 * @author Joel Rodrigues Moreira on 12/01/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class IntegerTest extends AbstractTest {
    public IntegerTest() {
        super("www.test.com.br?empresa.dtSync.$lte=2020-06-11T08:53:41.988-0300&itens.produto.descricao=asdfasd&itens.qtde.$lt=965.90&empresa.sequencial=1950");
    }

    @Test
    public void testValidInteger() {
        this.printResult(getProjection(NotaFiscal.class));
    }
}
