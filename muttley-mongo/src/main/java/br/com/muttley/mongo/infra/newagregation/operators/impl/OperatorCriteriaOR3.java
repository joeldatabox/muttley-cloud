package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.mongo.infra.newagregation.projections.Criterion3;
import br.com.muttley.mongo.infra.newagregation.projections.ProjectionMetadata;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OperatorCriteriaOR3 extends AbstractOperatorImpl {
    public static final String wildcard = ".$or";

    public OperatorCriteriaOR3() {
        super(wildcard);
    }

    @Override
    public List<Criteria> extractCriteria(final ProjectionMetadata metadata, final String compositePropertyWithFather, final String key, final Object value) {
        /*final List<Criterion3> criterons = this.extractCriterionsArray(metadata, value.toString());
        criterons.forEach(it -> it.get);
        this.getQueryParams(value.toString()).stream().forEach(System.out::println);
        final String valueString = value.toString();
        //verificando se precisaremo fazer recusão para arrays internos
        if (valueString.contains("[") || valueString.contains("]")) {
            //verificando se tem a quantidade de abertura e fechamento de chaves
            if (!(StringUtils.countOccurrencesOf(valueString, "[") == StringUtils.countOccurrencesOf(valueString, "]"))) {
                throw new MuttleyBadRequestException(this.getClass(), null, "a expressão errada é inválida, por favor verifique")
                        .addDetails("expression", valueString);
            }
            //nesse momento teremos que pegar subblocos de informações
            //para percorrer recursivamente
            //1º [ e o ] e chamamos a cascata de informação com recursividade

        }*/
        return new LinkedList<>();
    }

    @Override
    public boolean isTypeArray() {
        return true;
    }
}
