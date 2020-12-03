package br.com.muttley.mongo.infra.test.projections;

import br.com.muttley.mongo.infra.operators.Operator2;

/**
 * @author Joel Rodrigues Moreira on 02/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Criterion2 {
    Operator2 getOperator();

    Object getValue();
}
