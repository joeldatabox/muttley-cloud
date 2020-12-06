package br.com.muttley.mongo.infra.operators.impl;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.operators.Operator2;
import br.com.muttley.mongo.infra.test.url.paramvalue.NewQueryParam;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OperatorCriteriaOR2 implements Operator2 {
    private static final String wildcard = ".$or";

    @Override
    public String getWildcard() {
        return wildcard;
    }

    @Override
    public List<AggregationOperation> extractAggregations(EntityMetaData entityMetaData, String key, Object value) {
        return this.extractAggregations(entityMetaData, key, key, value);
    }

    @Override
    public List<AggregationOperation> extractAggregations(EntityMetaData entityMetaData, String compositePropertyWithFather, String key, Object value) {
        return new LinkedList<>();
    }

    @Override
    public List<Criteria> extractCriteria(EntityMetaData entityMetaData, String key, Object value) {
        return this.extractCriteria(entityMetaData, key, key, value);
    }

    @Override
    public List<Criteria> extractCriteria(EntityMetaData entityMetaData, String compositePropertyWithFather, String key, Object value) {
        this.getQueryParams(value.toString()).stream().forEach(System.out::println);
        return new LinkedList<>();
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
