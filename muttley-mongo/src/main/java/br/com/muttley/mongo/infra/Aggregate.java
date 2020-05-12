package br.com.muttley.mongo.infra;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.count;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

public final class Aggregate {
    private static final Pattern regexCheckProject = Pattern.compile("[^0-9_\\s\\_;_&_/_\\]_@_&]((([\\w_-]*)\\.){2,})+\\$");

    public static final List<AggregationOperation> createAggregations(final Class clazz, final Map<String, Object> queryParams) {
        return createAggregationsDefault(false, clazz, queryParams);
    }

    public static final List<AggregationOperation> createAggregationsCount(final Class clazz, final Map<String, Object> queryParams) {
        final List<AggregationOperation> list = createAggregationsDefault(true, clazz, queryParams);
        list.add(count().as("count"));
        return list;
    }

    private static final List<AggregationOperation> createAggregationsDefault(final boolean isCount, final Class clazz, final Map<String, Object> queryParams) {
        final Map<String, Map<Operator, Object>> trimap = checkCriterios(queryParams);
        final List<AggregationOperation> aggregations = new ArrayList<>(5);
        //verificando se é necessário fazer gabiarra do lookup
        //if (isRequiredProject(queryParams)) {
        aggregations.addAll(createProjectFrom(clazz, queryParams));
        //}
        SkipOperation skipOperation = null;
        LimitOperation limitOperation = null;
        SortOperation sortOperationAsc = null;
        SortOperation sortOperationDesc = null;
        for (final String key : trimap.keySet()) {
            final Map<Operator, Object> map = trimap.get(key);
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
                    default:
                        aggregations.add(match(extractCriteria(operation, key, value)));
                }

            }
        }
        if (!isCount) {
            if (sortOperationAsc != null) aggregations.add(sortOperationAsc);
            if (sortOperationDesc != null) aggregations.add(sortOperationDesc);
            if (skipOperation != null) aggregations.add(skipOperation);
            if (limitOperation != null) aggregations.add(limitOperation);
        }
        return aggregations;
    }

    private static final List<AggregationOperation> createProjectFrom(final Class clazz, final Map<String, Object> queryParams) {
        //pegando os capos para lookup
        final List<String> fieldForLookup = new ArrayList<>(1);
        for (final String key : queryParams.keySet()) {
            final Matcher matcher = regexCheckProject.matcher(queryParams.get(key).toString());
            while (matcher.find()) {
                fieldForLookup.add(matcher.group());
            }
        }

        final Field[] fields = Stream.of(clazz.getDeclaredFields()).toArray(Field[]::new);
        final String[] nameVariables = Stream.of(fields).map(Field::getName).toArray(String[]::new);
        final Set<String> projectionsKey = new HashSet<>(fieldForLookup.size() * 2);
        final List<AggregationOperation> list = new ArrayList<>(fieldForLookup.size() * 4);
        fieldForLookup.forEach(ffl -> {
            final String where = ffl.substring(0, ffl.indexOf(".$")).substring(0, ffl.indexOf("."));
            /*final String[] where = Stream.of(split(";;", ffl.substring(1, ffl.length() - 1)))
                    .map(f -> f.substring(0, f.indexOf(".$")))
                    .map(f -> f.substring(0, f.indexOf(".")))
                    .toArray(String[]::new);*/
            if (!projectionsKey.contains(where)) {
                projectionsKey.add(where);

                final ProjectionOperation projectToArray = project(nameVariables);
                final ProjectionOperation projectArrayElementAt = project(nameVariables);


                list.add(projectToArray.and(context -> new org.bson.Document("", "")));
                list.add(projectToArray.and(context -> new org.bson.Document("$objectToArray", "$" + where)).as(where));
                list.add(projectArrayElementAt.and(context -> new org.bson.Document("$arrayElemAt", asList("$" + where + ".v", 1))).as(where));
                //pegando o nome da collection para fazer lookup
                final String from = Stream.of(fields).filter(fld -> fld.getName().equals(where)).findFirst().map(fld -> getCollectionName(fld.getType())).get();
                //fazendo lookup
                list.add(lookup(from, where, "_id", where));
                list.add(unwind("$" + where));
            }
        });
        return list;
    }

    /**
     * Verifica se é necessario fazer projeção para lookup utilizando uma expressão regular <br/>
     * <p>Por exemplo se conter o pelomenos um parametro com o seguinte padrão categoria.nome.$contains=a, devemos usar o lookup<p/>
     */
    public static final boolean isRequiredProject(Map<String, Object> queryParams) {
        for (final String key : queryParams.keySet()) {
            if (regexCheckProject.matcher(queryParams.get(key).toString()).find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Cria criteria com base nos parametros
     */
    private static final Criteria extractCriteria(final Operator operator, final String key, final Object value) {
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
                if (text == null || text.length() <= 2) {
                    throw new MuttleyBadRequestException(null, "$or", "Erro ao executar a consulta").addDetails("$or", "informe algum parametro valido para o operador $or");
                }
                final String[] allCriterions = split(";;", text.substring(1, text.length() - 1));
                final Criteria[] criterionsOr = new Criteria[allCriterions.length];
                for (int i = 0; i < allCriterions.length; i++) {
                    final String expr[] = split("=", allCriterions[i]);
                    //evitando que algum animal passe uma string vazia
                    if (expr.length > 1) {
                        //extraindo os criterios do or
                        criterionsOr[i] = extractCriteria(Operator.of(expr[0]), createKey(expr[0]), expr[1]);
                    } else {
                        //string ta vazia mano
                        criterionsOr[i] = extractCriteria(Operator.of(expr[0]), createKey(expr[0]), "");
                    }
                }
                return new Criteria().orOperator(criterionsOr);
            }
            default:
                throw new IllegalArgumentException("Case not foud");
        }
    }

    /**
     * Organiza as operação necessarias em um trimap
     */
    private static final Map<String, Map<Operator, Object>> checkCriterios(final Map<String, Object> queryParams) {
        final Map<String, Map<Operator, Object>> trimap = new HashMap<>();

        //organizando os parametros
        queryParams.forEach((key, value) -> {
            final String keyTrimap = createKey(key);
            Operator operator = Operator.of(key);
            if (operator == null) {
                operator = Operator.IS;
                LogFactory.getLog(Aggregate.class).error("Atenção, operador de agregação não encontrado. Será adicionado o .$is");
            }
            switch (operator) {
                case CONTAINS:
                    addParam(trimap, keyTrimap, Operator.CONTAINS, value);
                    break;
                case GTE:
                    addParam(trimap, keyTrimap, Operator.GTE, value);
                    break;
                case LT:
                    addParam(trimap, keyTrimap, Operator.LT, value);
                    break;
                case IN:
                    addParam(trimap, keyTrimap, Operator.IN, split(String.valueOf(value)));
                    break;
                case IS:
                    addParam(trimap, keyTrimap, Operator.IS, value);
                    break;
                case OR:
                    addParam(trimap, keyTrimap, Operator.OR, value);
                    break;
                case SKIP:
                    addParam(trimap, keyTrimap, Operator.SKIP, value);
                    break;
                case LIMIT:
                    addParam(trimap, keyTrimap, Operator.LIMIT, value);
                    break;
                case ORDER_BY_ASC:
                    addParam(trimap, keyTrimap, Operator.ORDER_BY_ASC, split(String.valueOf(value)));
                    break;
                case ORDER_BY_DESC:
                    addParam(trimap, keyTrimap, Operator.ORDER_BY_DESC, split(String.valueOf(value)));
                    break;
                default:
                    throw new MuttleyBadRequestException(null, null, "A requisição contem criterios inválidos");
            }
        });
        return trimap;
    }

    /**
     * Cria uma chave para o trimap que organiza a agregação a ser executada
     */
    private static final String createKey(String key) {
        for (final Operator o : Operator.values()) {
            key = key.replace(o.toString(), "");
        }
        return key;
    }

    private static final void addParam(final Map<String, Map<Operator, Object>> trimap, final String key, final Operator operator, final Object value) {
        if (trimap.containsKey(key)) {
            trimap.get(key).put(operator, value);
        } else {
            final HashMap<Operator, Object> mp = new HashMap<>();
            mp.put(operator, value);
            trimap.put(key, mp);
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
        if (value != null && !value.isEmpty()) {
            return value.split(regex);
        }
        return null;
    }

    private static final String getCollectionName(final Class clazz) {
        return ((Document) clazz.getAnnotation(Document.class)).collection();
    }
}
