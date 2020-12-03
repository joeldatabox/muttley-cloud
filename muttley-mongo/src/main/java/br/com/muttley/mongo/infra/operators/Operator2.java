package br.com.muttley.mongo.infra.operators;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.operators.impl.OperatorCriteriaCONTAINS2;
import br.com.muttley.mongo.infra.operators.impl.OperatorCriteriaGT2;
import br.com.muttley.mongo.infra.operators.impl.OperatorCriteriaGTE2;
import br.com.muttley.mongo.infra.operators.impl.OperatorCriteriaIN2;
import br.com.muttley.mongo.infra.operators.impl.OperatorCriteriaIS2;
import br.com.muttley.mongo.infra.operators.impl.OperatorCriteriaLIMIT2;
import br.com.muttley.mongo.infra.operators.impl.OperatorCriteriaLT2;
import br.com.muttley.mongo.infra.operators.impl.OperatorCriteriaLTE2;
import br.com.muttley.mongo.infra.operators.impl.OperatorCriteriaOR2;
import br.com.muttley.mongo.infra.operators.impl.OperatorCriteriaORDER_BY_ASC2;
import br.com.muttley.mongo.infra.operators.impl.OperatorCriteriaORDER_BY_DESC2;
import br.com.muttley.mongo.infra.operators.impl.OperatorCriteriaSKIP2;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 02/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Operator2 {
    public static final Operator2 GTE = new OperatorCriteriaGTE2();
    public static final Operator2 LTE = new OperatorCriteriaLTE2();
    public static final Operator2 GT = new OperatorCriteriaGT2();
    public static final Operator2 LT = new OperatorCriteriaLT2();
    public static final Operator2 IN = new OperatorCriteriaIN2();
    public static final Operator2 CONTAINS = new OperatorCriteriaCONTAINS2();
    public static final Operator2 IS = new OperatorCriteriaIS2();
    public static final Operator2 SKIP = new OperatorCriteriaSKIP2();
    public static final Operator2 LIMIT = new OperatorCriteriaLIMIT2();
    public static final Operator2 OR = new OperatorCriteriaOR2();
    public static final Operator2 ORDER_BY_ASC = new OperatorCriteriaORDER_BY_ASC2();
    public static final Operator2 ORDER_BY_DESC = new OperatorCriteriaORDER_BY_DESC2();

    String getWildcard();

    List<AggregationOperation> extractAggregations(final EntityMetaData entityMetaData, final String key, final Object value);

    List<AggregationOperation> extractAggregations(final EntityMetaData entityMetaData, final String compositePropertyWithFather, String key, final Object value);

    List<Criteria> extractCriteria(final EntityMetaData entityMetaData, final String key, final Object value);

    List<Criteria> extractCriteria(final EntityMetaData entityMetaData, final String compositePropertyWithFather, String key, final Object value);


    public static Operator2[] values() {
        return new Operator2[]{
                GTE,
                LTE,
                GT,
                LT,
                IN,
                CONTAINS,
                IS,
                SKIP,
                LIMIT,
                OR,
                ORDER_BY_ASC,
                ORDER_BY_DESC
        };
    }

    public static Operator2 of(String value) {
        if (value.contains(".$id")) {
            value = value.replace(".$id", "");
        }
        if (value.contains(".$")) {
            value = value.substring(value.indexOf(".$"));
        } else if (value.contains("$")) {
            value = value.substring(value.indexOf("$"));
        }

        switch (value.toLowerCase()) {
            case ".$gte":
            case "$gte":
                return GTE;
            case ".$lte":
            case "$lte":
                return LTE;
            case ".$gt":
            case "$gt":
                return GT;
            case ".$lt":
            case "$lt":
                return LT;
            case ".$in":
            case "$in":
                return IN;
            case ".$contains":
            case "$contains":
                return CONTAINS;
            case ".$is":
            case "$is":
                return IS;
            case "$skip":
                return SKIP;
            case "$limit":
                return LIMIT;
            case ".$or":
            case "$or":
                return OR;
            case "$orderByAsc":
            case "$orderbyasc":
                return ORDER_BY_ASC;
            case "$orderByDesc":
            case "$orderbydesc":
                return ORDER_BY_DESC;
            default:
                return IS;

        }
    }

    default boolean containsOperator(final String value) {
        return value.contains(".$gte") ||
                value.contains("$gte") ||
                value.contains(".$lte") ||
                value.contains("$lte") ||
                value.contains(".$gt") ||
                value.contains("$gt") ||
                value.contains(".$lt") ||
                value.contains("$lt") ||
                value.contains(".$in") ||
                value.contains("$in") ||
                value.contains(".$contains") ||
                value.contains("$contains") ||
                value.contains(".$is") ||
                value.contains("$is") ||
                value.contains("$skip") ||
                value.contains("$limit") ||
                value.contains(".$or") ||
                value.contains("$or") ||
                value.contains("$orderByAsc") ||
                value.contains("$orderByDesc");
    }

    default boolean isOperator(final String value) {
        return value.equalsIgnoreCase(".$gte") ||
                value.equalsIgnoreCase("$gte") ||
                value.equalsIgnoreCase(".$lte") ||
                value.equalsIgnoreCase("$lte") ||
                value.equalsIgnoreCase(".$gt") ||
                value.equalsIgnoreCase("$gt") ||
                value.equalsIgnoreCase(".$lt") ||
                value.equalsIgnoreCase("$lt") ||
                value.equalsIgnoreCase(".$in") ||
                value.equalsIgnoreCase("$in") ||
                value.equalsIgnoreCase(".$contains") ||
                value.equalsIgnoreCase("$contains") ||
                value.equalsIgnoreCase(".$is") ||
                value.equalsIgnoreCase("$is") ||
                value.equalsIgnoreCase("$skip") ||
                value.equalsIgnoreCase("$limit") ||
                value.equalsIgnoreCase(".$or") ||
                value.equalsIgnoreCase("$or") ||
                value.equalsIgnoreCase("$orderByAsc") ||
                value.equalsIgnoreCase("$orderByDesc");

    }
}
