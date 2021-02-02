package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;

import java.util.stream.Stream;

/**
 * @author Joel Rodrigues Moreira on 01/02/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class OperatorCriteriaWithArray extends AbstractOperatorImpl {

    public OperatorCriteriaWithArray(final String wildcard) {
        super(wildcard);
    }

    protected String[] splitArray(final String array) {
        if (!(array.startsWith("'") && array.endsWith("'"))) {
            throw new MuttleyBadRequestException(null, null, "a expressão de consulta é inválida, por favor verifique")
                    .addDetails("expression", array)
                    .addDetails("obs", "cada item do array deve estar entre aspas e separado por -> ;");
        }
        return Stream.of(array.substring(1, array.length() - 1).split("';'"))
                .toArray(String[]::new);
    }

}
