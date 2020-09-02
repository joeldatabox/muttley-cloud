package br.com.muttley.mongo.query;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.operators.Operator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;


/**
 * @author Joel Rodrigues Moreira on 29/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project agrifocus-cloud
 */
@Getter
@Setter
public class QueryBuilder {
    //private static final String regex = "(.\\$gte)|(.\\$lt)|(/$orderByAsc)";
    private static final String DATE_REGEX = "(\\d{4}|\\d{5}|\\d{6}|\\d{7})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|[3][01])";
    private long level = 0;
    private int order = 0;
    private int lastOrderGenerate = -1;
    private EntityMetaData metaData;
    private String name;
    private String paramKey;
    private Set<QueryBuilder> subItens = new LinkedHashSet<>();
    private List<String> criterion;
    private List<CriterionItem> criterions = new LinkedList<>();

    public static QueryBuilder from(EntityMetaData entityMetaData, Map<String, List<String>> queriesParam) {
        final QueryBuilder queryBuilder = new QueryBuilder();
        queriesParam.entrySet().forEach(entrySet -> {
            //extraindo o nome do campo
            Operator operator = Operator.of(entrySet.getKey());
            final String keyTrimap = replaceAllOperators(entrySet.getKey());

            queryBuilder.addCriterio(queryBuilder, keyTrimap, new CriterionItem(operator, entrySet.getValue()));
        });
        return queryBuilder;
    }

    /**
     * Metodo que adiciona critério de maneira recursiva
     */
    private QueryBuilder addCriterio(final QueryBuilder builder, final String key, final CriterionItem criterionItem) {
        if (builder.contains(key)) {
            //adiciona os demais criterios aqui
            builder.addCriterionItem(criterionItem);
        } else if (key.contains(".")) {
            //pegando o nome atual da chave
            final String currentName = key.substring(0, key.indexOf("."));
            final QueryBuilder current;
            //já existe esse chave no nível atual?
            if (builder.contains(currentName)) {
                //recuperando a chave no nível atual
                current = builder.getByKey(currentName);
            } else {
                //se chegou aqui é sinal que não existe ainda essa chave no nível atual
                current = new QueryBuilder();
                current.setName(currentName);
                builder.add(current);
            }
            current.addCriterio(current, key.substring(key.indexOf(".") + 1), criterionItem);

            /*//verificando se já existe esse campo
            if (builder.contains(keys[0])) {
                //pegando o campo existente para evitar duplicações desnecessárias
                current = builder.getByKey(keys[0]);
            } else {
                //se chegou até aqui é sinal que não existe esse campo ainda

            }
            final QueryBuilder qb = new QueryBuilder();
            //adicionado ao principal
            queryBuilder.add(qb);
            qb.setParamKey(keyTrimap);
            qb.setName(keys[0]);
            qb.setMetaData(entityMetaData.getFieldByName(keys[0]));

            //variavel de controle para a ultima posição
            QueryBuilder last = qb;
            //gerando as demais keys
            //como o primeiro item já foi inserido, podemos pular o mesmo*/
        } else {
            final QueryBuilder current = new QueryBuilder();
            current.setName(key);
            builder.add(current);
            current.addCriterionItem(criterionItem);
        }
        return builder;
    }

    private QueryBuilder addCriterionItem(final CriterionItem item) {
        if (item != null) {
            this.criterions.add(item);
        }
        return this;
    }

    /**
     * Metodo que adiciona critério de maneira recursiva
     */
    private QueryBuilder addCriterio2(final QueryBuilder builder, final String key) {
        if (builder.contains(key)) {
            //adiciona os demais criterios aqui
        } else if (key.contains(".")) {
            final String currentName = key.substring(0, key.indexOf("."));
            final QueryBuilder current = new QueryBuilder();
            current.setName(key.substring(0, key.indexOf(".")));
            current.addCriterio2(current, key.substring(key.indexOf(".") + 1));
            builder.add(current);
            /*//verificando se já existe esse campo
            if (builder.contains(keys[0])) {
                //pegando o campo existente para evitar duplicações desnecessárias
                current = builder.getByKey(keys[0]);
            } else {
                //se chegou até aqui é sinal que não existe esse campo ainda

            }
            final QueryBuilder qb = new QueryBuilder();
            //adicionado ao principal
            queryBuilder.add(qb);
            qb.setParamKey(keyTrimap);
            qb.setName(keys[0]);
            qb.setMetaData(entityMetaData.getFieldByName(keys[0]));

            //variavel de controle para a ultima posição
            QueryBuilder last = qb;
            //gerando as demais keys
            //como o primeiro item já foi inserido, podemos pular o mesmo*/
        } else {
            final QueryBuilder current = new QueryBuilder();
            current.setName(key);
            builder.add(current);
        }
        return builder;
    }

    /**
     * Verifica se uma determinada chave já voi inserida como campos internos
     */
    public boolean contains(final String key) {
        return this.contains(key, this);
    }

    private boolean contains(final String key, final QueryBuilder builder) {
        //se o nome tiver . é necessário fazezr recursividade1
        if (key.contains(".")) {
            //pegando o primeiro nome
            final String currentPath = key.substring(0, key.indexOf("."));

            final QueryBuilder currentQueryBuilder = builder.getSubItens().parallelStream().filter(it -> it.getName().equals(currentPath))
                    .findFirst()
                    .orElse(null);
            if (currentQueryBuilder != null) {
                return contains(key.replace(currentPath + ".", ""), currentQueryBuilder);
            }
            return false;
        } else {
            if (!builder.containsSubItens()) {
                return false;
            }
            return builder
                    .getSubItens()
                    .parallelStream()
                    .filter(it -> {
                        final String referencedName = "$" + it.getName();
                        return it.getName().equals(key) || referencedName.equals(key);
                    })
                    .count() > 0;
        }
    }

    /**
     * Retorna um campo com base na chave passada como parametro
     */
    public QueryBuilder getByKey(final String key) {
        return this.getByKey(key, this);
    }

    private QueryBuilder getByKey(final String key, final QueryBuilder builder) {
        //se o nome tiver . é necessário fazezr recursividade1
        if (key.contains(".")) {
            //pegando o primeiro nome
            final String currentPath = key.substring(0, key.indexOf("."));

            final QueryBuilder currentQueryBuilder = builder.getSubItens().parallelStream().filter(it -> it.getName().equals(currentPath))
                    .findFirst()
                    .orElse(null);
            if (currentQueryBuilder != null) {
                return getByKey(key.replace(currentPath + ".", ""), currentQueryBuilder);
            }
            return null;
        } else {
            if (!builder.containsSubItens()) {
                return null;
            }
            return builder
                    .getSubItens()
                    .parallelStream()
                    .filter(it -> {
                        final String referencedName = "$" + it.getName();
                        return it.getName().equals(key) || referencedName.equals(key);
                    }).findFirst()
                    .orElse(null);
        }
    }

    public boolean containsSubItens() {
        return !CollectionUtils.isEmpty(this.subItens);
    }

    private QueryBuilder add(QueryBuilder value) {
        //o level é definido pelo level atual + 1
        value.setLevel(this.getLevel() + 1);
        value.setOrder(this.generateOrder());
        this.getSubItens().add(value);
        return this;
    }

    private int generateOrder() {
        this.lastOrderGenerate++;
        return new Integer(this.lastOrderGenerate);
    }

    /**
     * Remove qualquer operador presente em uma string
     * por exemplo:
     *
     * @param value => "pessoa.nome.$is"
     * @return "pessoa.nome"
     */
    public static final String replaceAllOperators(final String value) {
        if (!value.contains(".$")) {
            return value;
        }
        String result = value;
        //pegando todos os operadores
        final String[] operators = Stream.of(Operator.values())
                //pegando a representação em string
                .map(Operator::toString)
                .toArray(String[]::new);
        for (final String widcard : operators) {
            result = result.replace(widcard, "");
        }
        return result;
    }


    @Override
    public String toString() {
        return "QueryBuilder{" +
                "level=" + level +
                ", order=" + order +
                ", name='" + name + '\'' +
                ", paramKey='" + paramKey + '\'' +
                ", subItens=" + subItens +
                ", criterion=" + criterion +
                '}';
    }

    @Getter
    public static class CriterionItem {
        final Operator operator;
        final Object value;

        public CriterionItem(Operator operator, Object value) {
            this.operator = operator;
            this.value = value;
        }

        @Override
        public String toString() {
            return "{" +
                    operator.toString() +
                    " : " + value +
                    '}';
        }
    }
}
