package br.com.muttley.mongo.service.infra;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by master on 06/03/17.
 */
public class QueryBuilder {

    public static Query createQuery(final Map<String, Object> queryParams) {
        final Map<String, Map<Operators, Object>> trimap = new HashMap<>();
        queryParams.forEach((key, value) -> {
            final String k = key(key);

            if (key.endsWith(Operators.CONTAINS.toString())) {
                addParam(trimap, k, Operators.CONTAINS, value);
            } else if (key.endsWith(Operators.GTE.toString())) {
                addParam(trimap, k, Operators.GTE, value);
            } else if (key.endsWith(Operators.LT.toString())) {
                addParam(trimap, k, Operators.LT, value);
            } else if (key.endsWith(Operators.IN.toString())) {
                addParam(trimap, k, Operators.IN, split(String.valueOf(value)));
            } else if (key.endsWith(Operators.IS.toString()) || !key.contains("$")) {
                addParam(trimap, k, Operators.IS, value);
            } else if (key.endsWith(Operators.OR.toString())) {
                addParam(trimap, k, Operators.OR, value);
            } else if (key.endsWith(Operators.SKIP.toString())) {
                addParam(trimap, k, Operators.SKIP, value);
            } else if (key.endsWith(Operators.LIMIT.toString())) {
                addParam(trimap, k, Operators.LIMIT, value);
            } else if (key.endsWith(Operators.ORDER_BY_ASC.toString())) {
                addParam(trimap, k, Operators.ORDER_BY_ASC, split(String.valueOf(value)));
            } else if (key.endsWith(Operators.ORDER_BY_DESC.toString())) {
                addParam(trimap, k, Operators.ORDER_BY_DESC, split(String.valueOf(value)));
            }
        });
        final Query query = new Query();
        trimap.forEach((key, map) -> {
            //Criteria cri = where(key);
            map.forEach((operation, value) -> {
                if (Operators.SKIP.equals(operation)) {
                    query.skip(Integer.valueOf(value.toString()));
                } else if (Operators.LIMIT.equals(operation)) {
                    query.limit(Integer.valueOf(value.toString()));
                } else if (Operators.ORDER_BY_ASC.equals(operation)) {
                    query.with(new Sort(Sort.Direction.ASC, (String[]) value));
                } else if (Operators.ORDER_BY_DESC.equals(operation)) {
                    query.with(new Sort(Sort.Direction.DESC, (String[]) value));
                } else {
                    query.addCriteria(extractCriteria(operation, key, value));
                }
            });
            //query.addCriteria(cri);
        });
        return query;
    }

    private static Criteria extractCriteria(final Operators operator, final String key, final Object value) {
        switch (operator) {
            case CONTAINS:
                return new Criteria(key).regex(value.toString(), "si");
            case GTE:
                return new Criteria(key).gte(value);
            case LT:
                return new Criteria(key).lt(value);
            case IN:
                return new Criteria(key).in((Object[]) value);
            case IS:
                return new Criteria(key).is(value);
            case OR: {
                final String text = value.toString();
                final String[] allCriterions = split(";;", text.substring(1, text.length() - 1));
                final Criteria[] criterionsOr = new Criteria[allCriterions.length];
                for (int i = 0; i < allCriterions.length; i++) {
                    final String expr[] = split("=", allCriterions[i]);
                    //evitando que algum animal passe uma string vazia
                    if (expr.length > 1) {
                        criterionsOr[i] = extractCriteria(Operators.of(expr[0]), key(expr[0]), expr[1]);
                    } else {
                        //string ta vazia mano
                        criterionsOr[i] = extractCriteria(Operators.of(expr[0]), key(expr[0]), "");
                    }
                }
                return new Criteria().orOperator(criterionsOr);
            }
            default:
                throw new IllegalArgumentException("Case not foud");
        }
    }

    private static void addParam(final Map<String, Map<Operators, Object>> trimap, final String key, final Operators operator, final Object value) {
        if (trimap.containsKey(key)) {
            trimap.get(key).put(operator, value);
        } else {
            HashMap<Operators, Object> mp = new HashMap<>();
            mp.put(operator, value);
            trimap.put(key, mp);
        }
    }

    private static String key(String key) {
        for (final Operators o : Operators.values()) {
            key = key.replace(o.toString(), "");
        }
        return key;
    }

    private static String[] split(String value) {
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

    private static String[] split(final String regex, final String value) {
        if (value != null && !value.isEmpty()) {
            return value.split(regex);
        }
        return null;
    }
}
