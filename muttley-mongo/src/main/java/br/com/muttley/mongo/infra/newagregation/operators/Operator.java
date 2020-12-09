package br.com.muttley.mongo.infra.newagregation.operators;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaCONTAINS;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaGT;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaGTE;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaIN;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaIS;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaLIMIT;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaLT;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaLTE;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaOR;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaORDER_BY_ASC;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaORDER_BY_DESC;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaSKIP;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Operator<T> {
    public static final Operator GTE = new OperatorCriteriaGTE();
    public static final Operator LTE = new OperatorCriteriaLTE();
    public static final Operator GT = new OperatorCriteriaGT();
    public static final Operator LT = new OperatorCriteriaLT();
    public static final Operator IN = new OperatorCriteriaIN();
    public static final Operator CONTAINS = new OperatorCriteriaCONTAINS();
    public static final Operator IS = new OperatorCriteriaIS();
    public static final Operator SKIP = new OperatorCriteriaSKIP();
    public static final Operator LIMIT = new OperatorCriteriaLIMIT();
    public static final Operator OR = new OperatorCriteriaOR();
    public static final Operator ORDER_BY_ASC = new OperatorCriteriaORDER_BY_ASC();
    public static final Operator ORDER_BY_DESC = new OperatorCriteriaORDER_BY_DESC();

    String getWildcard();

    T extract(final EntityMetaData entityMetaData, final String key, final Object value);

    T extract(final EntityMetaData entityMetaData, final String compositePropertyWithFather, String key, final Object value);

    boolean isCriteriaOperation();

    boolean isAggregationOperation();

    public static Operator[] values() {
        return new Operator[]{
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

    public static Operator of(String value) {
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
