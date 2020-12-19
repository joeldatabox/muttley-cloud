package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.newagregation.operators.Operator2;
import br.com.muttley.mongo.infra.newagregation.paramvalue.NewQueryParam;
import br.com.muttley.mongo.infra.newagregation.projections.Projection2;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@EqualsAndHashCode(of = "wildcard")
public class OperatorCriteriaOR2 implements Operator2 {
    private static final String wildcard = ".$or";

    @Override
    public String getWildcard() {
        return wildcard;
    }

    @Override
    public List<AggregationOperation> extractAggregations(final Projection2 projection, final EntityMetaData entityMetaData, final String key, final Object value) {
        return this.extractAggregations(projection, entityMetaData, key, key, value);
    }

    @Override
    public List<AggregationOperation> extractAggregations(final Projection2 projection, final EntityMetaData entityMetaData, final String compositePropertyWithFather, final String key, final Object value) {
        return new LinkedList<>();
    }

    @Override
    public List<Criteria> extractCriteria(final Projection2 projection, final EntityMetaData entityMetaData, final String key, final Object value) {
        return this.extractCriteria(projection, entityMetaData, key, key, value);
    }

    @Override
    public List<Criteria> extractCriteria(final Projection2 projection, final EntityMetaData entityMetaData, final String compositePropertyWithFather, final String key, final Object value) {
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

        }
        return new LinkedList<>();
    }

    @Override
    public boolean isTypeArray() {
        return true;
    }

    private List<NewQueryParam> getQueryParams(final String params) {

        List<NewQueryParam> queryParams = new LinkedList<>();
        if (params.contains(";;")) {
            final String[] itens = params.split(";;");
            Stream.of(itens).parallel()
                    .forEach(it -> {
                        queryParams.add(new NewQueryParam(it.substring(0, it.indexOf(":")), it.substring(it.indexOf(":") + 1)));
                    });
        }
        return queryParams;
    }
}
