package br.com.muttley.mongo.infra;


import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import io.jsonwebtoken.lang.Collections;
import lombok.Getter;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static br.com.muttley.mongo.infra.Operator.CONTAINS;
import static br.com.muttley.mongo.infra.Operator.GTE;
import static br.com.muttley.mongo.infra.Operator.IN;
import static br.com.muttley.mongo.infra.Operator.IS;
import static br.com.muttley.mongo.infra.Operator.LIMIT;
import static br.com.muttley.mongo.infra.Operator.LT;
import static br.com.muttley.mongo.infra.Operator.OR;
import static br.com.muttley.mongo.infra.Operator.ORDER_BY_ASC;
import static br.com.muttley.mongo.infra.Operator.ORDER_BY_DESC;
import static br.com.muttley.mongo.infra.Operator.SKIP;
import static br.com.muttley.mongo.infra.Operator.of;
import static br.com.muttley.mongo.infra.Operator.values;
import static java.util.Arrays.asList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.count;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 04/06/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project invistate-cloud
 */
public class AggregationUtils {

    public static final List<AggregationOperation> createAggregations(final EntityMetaData entityMetaData, final List<AggregationOperation> pipelines, final Map<String, String> queryParams) {
        return createAggregationsDefault(entityMetaData, pipelines, false, queryParams);
    }

    public static final List<AggregationOperation> createAggregationsCount(final EntityMetaData entityMetaData, final List<AggregationOperation> pipelines, final Map<String, String> queryParams) {
        final List<AggregationOperation> list = createAggregationsDefault(entityMetaData, pipelines, true, queryParams);
        list.add(count().as("count"));
        return list;
    }

    private static List<AggregationOperation> createAggregationsDefault(final EntityMetaData entityMetaData, final List<AggregationOperation> pipelines, final boolean isCount, final Map<String, String> queryParams) {
        final Map<String, Map<Operator, Object>> triMap = processCriterions(queryParams);
        final List<AggregationOperation> aggregations = new ArrayList<>(5);


        SkipOperation skipOperation = null;
        LimitOperation limitOperation = null;
        SortOperation sortOperationAsc = null;
        SortOperation sortOperationDesc = null;

        for (final String key : triMap.keySet()) {
            final Map<Operator, Object> map = triMap.get(key);
            for (final Operator operation : map.keySet()) {
                final Object value = map.get(operation);

                switch (operation) {
                    case SKIP:
                        if (!isCount)
                            skipOperation = skip(Long.valueOf(value.toString()));
                        break;
                    case LIMIT:
                        if (!isCount)
                            limitOperation = limit(Long.valueOf(value.toString()));
                        break;
                    case ORDER_BY_ASC:
                        if (!isCount)
                            sortOperationAsc = sort(Sort.Direction.ASC, (String[]) value);
                        break;
                    case ORDER_BY_DESC:
                        if (!isCount)
                            sortOperationDesc = sort(Sort.Direction.DESC, (String[]) value);
                        break;
                    default: {
                        final List<Pipelines> pipes = extractCriteria(entityMetaData, isEmpty(pipelines), operation, key, value);
                        if (pipes != null) {
                            pipes.forEach(it -> {
                                if (!Collections.isEmpty(it.getPipelines())) {
                                    aggregations.addAll(it.getPipelines());
                                }
                                aggregations.add(match(it.getCriteria()));
                            });
                        }
                    }
                }

            }
        }
        if (!isCount) {
            if (sortOperationAsc != null) aggregations.add(sortOperationAsc);
            if (sortOperationDesc != null) aggregations.add(sortOperationDesc);
            if (skipOperation != null) aggregations.add(skipOperation);
            if (limitOperation != null) aggregations.add(limitOperation);
        }
        if (aggregations.isEmpty()) {
            aggregations.add(skip(0L));
        }
        return aggregations;
    }

    /**
     * Organiza as operações necessárias para cada parametro passado
     */
    private static final Map<String, Map<Operator, Object>> processCriterions(final Map<String, String> queryParams) {
        final Map<String, Map<Operator, Object>> triMap = new HashMap();

        //percorrendo todos os itens
        queryParams.forEach((final String key, final String value) -> {
            Operator operator = of(key);
            final String keyTrimap = replaceAllOperators(key);
            if (operator == null) {
                operator = IS;
                LogFactory.getLog(AggregationUtils.class).error("Atenção, operador de agregação não encontrado. Será adicionado o .$is");
            }
            switch (operator) {
                case CONTAINS:
                    addParamInTriMap(triMap, keyTrimap, CONTAINS, value);
                    break;
                case GTE:
                    addParamInTriMap(triMap, keyTrimap, GTE, value);
                    break;
                case LT:
                    addParamInTriMap(triMap, keyTrimap, LT, value);
                    break;
                case IN:
                    addParamInTriMap(triMap, keyTrimap, IN, split(String.valueOf(value)));
                    break;
                case IS:
                    addParamInTriMap(triMap, keyTrimap, IS, value);
                    break;
                case OR:
                    addParamInTriMap(triMap, keyTrimap, OR, value);
                    break;
                case SKIP:
                    addParamInTriMap(triMap, keyTrimap, SKIP, value);
                    break;
                case LIMIT:
                    addParamInTriMap(triMap, keyTrimap, LIMIT, value);
                    break;
                case ORDER_BY_ASC:
                    addParamInTriMap(triMap, keyTrimap, ORDER_BY_ASC, split(String.valueOf(value)));
                    break;
                case ORDER_BY_DESC:
                    addParamInTriMap(triMap, keyTrimap, ORDER_BY_DESC, split(String.valueOf(value)));
                    break;
                default:
                    throw new MuttleyBadRequestException(null, null, "A requisição contem criterios inválidos");
            }
        });
        return triMap;
    }

    /**
     * Remove qualquer operador presente em uma stringo
     * por exemplo:
     *
     * @param value => "pessoa.nome.$is"
     * @return "pessoa.nome"
     */
    private static final String replaceAllOperators(final String value) {
        //lista de widcards
        final String[] operators = Stream.of(values()).map(Operator::toString).toArray(String[]::new);
        String result = value;
        //removendo tudo
        for (final String widcard : operators) {
            result = result.replace(widcard, "");
        }
        return result;
    }

    private static final void addParamInTriMap(Map<String, Map<Operator, Object>> triMap, final String key, final Operator operator, final Object value) {
        if (triMap.containsKey(key)) {
            triMap.get(key).put(operator, value);
        } else {
            final HashMap<Operator, Object> mp = new HashMap<>();
            mp.put(operator, value);
            triMap.put(key, mp);
        }
    }

    private static final String[] split(String value) {
        if (value != null && !value.isEmpty()) {
            final String first = value.substring(0, 1);
            if (first.equals(";") || first.equals("|")) {
                value = value.substring(1);
            }

            if (value.contains(";"))
                return value.split(";");
            if (value.contains("|"))
                return value.split("|");

            return new String[]{value};
        }
        return null;
    }

    private static final String[] split(final String regex, final String value) {
        if (!StringUtils.isEmpty(value)) {
            return value.split(regex);
        }
        return null;
    }

    private static final List<Pipelines> extractCriteria(final EntityMetaData entityMetaData, final boolean generatePipelines, final Operator operator, final String key, final Object value) {
        switch (operator) {
            case IS:
                return asList(new CustomCriteria(entityMetaData, key, extractIS(entityMetaData, key, value)).build(generatePipelines));
            case CONTAINS:
                return asList(new CustomCriteria(entityMetaData, key, extractCONTAINS(entityMetaData, key, value)).build(generatePipelines));
            case GTE:
                return asList(new CustomCriteria(entityMetaData, key, extractGTE(entityMetaData, key, value)).build(generatePipelines));
            case LTE:
                return asList(new CustomCriteria(entityMetaData, key, extractLTE(entityMetaData, key, value)).build(generatePipelines));
            case GT:
                return asList(new CustomCriteria(entityMetaData, key, extractGT(entityMetaData, key, value)).build(generatePipelines));
            case LT:
                return asList(new CustomCriteria(entityMetaData, key, extractLT(entityMetaData, key, value)).build(generatePipelines));
            case IN:
                return asList(new CustomCriteria(entityMetaData, key, extractIN(entityMetaData, key, value)).build(generatePipelines));
            case OR: {
                return extractOR(entityMetaData, generatePipelines, key, value);
            }
            default:
                throw new IllegalArgumentException("Case not foud");
        }
    }

    private static final Criteria extractIS(final EntityMetaData entityMetaData, final String key, final Object value) {
        final EntityMetaData m = entityMetaData.getFieldByName(key);
        if (m != null) {
            return new Criteria(key).is(m.converteValue(value));
        }
        return new Criteria(key).is(value);
    }

    private static final Criteria extractGTE(final EntityMetaData entityMetaData, final String key, final Object value) {
        final EntityMetaData m = entityMetaData.getFieldByName(key);
        if (m != null) {
            return new Criteria(key).gte(m.converteValue(value));
        }
        return new Criteria(key).gte(value);
    }

    private static final Criteria extractLTE(final EntityMetaData entityMetaData, final String key, final Object value) {
        final EntityMetaData m = entityMetaData.getFieldByName(key);
        if (m != null) {
            return new Criteria(key).lte(m.converteValue(value));
        }
        return new Criteria(key).lte(value);
    }

    private static final Criteria extractGT(final EntityMetaData entityMetaData, final String key, final Object value) {
        final EntityMetaData m = entityMetaData.getFieldByName(key);
        if (m != null) {
            return new Criteria(key).gt(m.converteValue(value));
        }
        return new Criteria(key).gt(value);
    }

    private static final Criteria extractLT(final EntityMetaData entityMetaData, final String key, final Object value) {
        final EntityMetaData m = entityMetaData.getFieldByName(key);
        if (m != null) {
            return new Criteria(key).lt(m.converteValue(value));
        }
        return new Criteria(key).lt(value);
    }

    private static final Criteria extractIN(final EntityMetaData entityMetaData, final String key, final Object value) {
        final EntityMetaData m = entityMetaData.getFieldByName(key);
        if (m != null) {
            return new Criteria(key).in(m.converteValue(value));
        }
        return new Criteria(key).in(value);
    }

    private static final Criteria extractCONTAINS(final EntityMetaData entityMetaData, final String key, final Object value) {
        final EntityMetaData m = entityMetaData.getFieldByName(key);
        if (m != null) {
            return new Criteria(key).regex(m.converteValue(value).toString(), "si");
        }
        return new Criteria(key).regex(value.toString(), "si");
    }

    private static final List<Pipelines> extractOR(final EntityMetaData entityMetaData, final boolean generatePipelines, final String key, final Object values) {

        final String text = values.toString();
        if (text == null || text.length() <= 2) {
            throw new MuttleyBadRequestException(null, "$or", "Erro ao executar a consulta").addDetails("$or", "informe algum parametro valido para o operador $or");
        }
        final String[] allCriterions = split(";;", text.substring(1, text.length() - 1));
        final List<Pipelines> pipelines = new ArrayList<>();
        for (int i = 0; i < allCriterions.length; i++) {
            final String expr[] = split("=", allCriterions[i]);
            //evitando que algum animal passe uma string vazia
            if (expr.length > 1) {
                //extraindo os criterios do or
                pipelines.addAll(extractCriteria(entityMetaData, generatePipelines, of(expr[0]), replaceAllOperators(expr[0]), expr[1]));
                //criterionsOr[i] = extractCriteria(entityMetaData, of(expr[0]), replaceAllOperators(expr[0]), expr[1]);
            } else {
                //string ta vazia mano
                pipelines.addAll(extractCriteria(entityMetaData, generatePipelines, of(expr[0]), replaceAllOperators(expr[0]), ""));
                //criterionsOr[i] = extractCriteria(entityMetaData, of(expr[0]), replaceAllOperators(expr[0]), "");
            }
        }
        return pipelines;

    }

    @Getter
    private static class CustomCriteria {
        private final EntityMetaData entityMetaData;
        private final String key;
        private final Criteria criteria;

        public CustomCriteria(final EntityMetaData entityMetaData, final String key, final Criteria criteria) {
            this.entityMetaData = entityMetaData;
            this.key = key;
            this.criteria = criteria;
            //this.build();
        }


        public Pipelines build(final boolean generatePipelines) {
            if (generatePipelines) {
                final List<AggregationOperation> aggregations = entityMetaData.createProjectFor(key);
                if (Collections.isEmpty(aggregations)) {
                    return new Pipelines(criteria);
                } else {
                    return new Pipelines(aggregations, criteria);
                }
            }
            return new Pipelines(criteria);
        }


        private String resolveExpression(final String expression) {
            if (expression.startsWith("#")) {
                return (String) new SpelExpressionParser().parseExpression(expression).getValue();
            }
            return expression;
        }

    }

    @Getter
    private static class Pipelines {
        private final List<AggregationOperation> pipelines;
        private final Criteria criteria;

        public Pipelines(final List<AggregationOperation> pipelines, final Criteria criteria) {
            this.pipelines = pipelines;
            this.criteria = criteria;
        }

        public Pipelines(Criteria criteria) {
            this(null, criteria);
        }
    }
}
