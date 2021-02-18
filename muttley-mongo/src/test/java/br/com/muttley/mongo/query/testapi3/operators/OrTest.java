package br.com.muttley.mongo.query.testapi3.operators;

import br.com.muttley.mongo.query.model2.NotaFiscal;
import br.com.muttley.mongo.query.testapi3.AbstractTest;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 01/02/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OrTest extends AbstractTest {
    public OrTest() {
        super("www.test.com.br?$or=[$or:[ciclo.descricao.$is:'descricao';ciclo.id.$is:'" + new ObjectId(new Date()) + "'];sequencial.$lt:'697';empresa.dtSync.$lt:'2020-06-11T08:53:41.988-0300']");
    }

    @Test
    public void testOROperator() {
        this.printResult(getProjection(NotaFiscal.class));
    }
}
