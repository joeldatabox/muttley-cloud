package br.com.muttley.mongo.query.projections;

import br.com.muttley.mongo.infra.operators.Operator;

/**
 * @author Joel Rodrigues Moreira on 02/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Criterion {
    Operator getOperator();

    Object getValue();
}
