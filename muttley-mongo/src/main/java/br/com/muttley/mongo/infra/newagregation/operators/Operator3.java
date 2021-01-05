package br.com.muttley.mongo.infra.newagregation.operators;

import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaCONTAINS3;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaGT3;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaGTE3;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaIN3;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaIS3;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaLIMIT3;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaLT3;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaLTE3;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaOR3;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaORDER_BY_ASC3;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaORDER_BY_DESC3;
import br.com.muttley.mongo.infra.newagregation.operators.impl.OperatorCriteriaSKIP3;
import br.com.muttley.mongo.infra.newagregation.projections.ProjectionMetadata;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 22/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Operator3 {
    public static final Operator3 GTE = new OperatorCriteriaGTE3();
    public static final Operator3 LTE = new OperatorCriteriaLTE3();
    public static final Operator3 GT = new OperatorCriteriaGT3();
    public static final Operator3 LT = new OperatorCriteriaLT3();
    public static final Operator3 IN = new OperatorCriteriaIN3();
    public static final Operator3 CONTAINS = new OperatorCriteriaCONTAINS3();
    public static final Operator3 IS = new OperatorCriteriaIS3();
    public static final Operator3 SKIP = new OperatorCriteriaSKIP3();
    public static final Operator3 LIMIT = new OperatorCriteriaLIMIT3();
    public static final Operator3 OR = new OperatorCriteriaOR3();
    public static final Operator3 ORDER_BY_ASC = new OperatorCriteriaORDER_BY_ASC3();
    public static final Operator3 ORDER_BY_DESC = new OperatorCriteriaORDER_BY_DESC3();

    String getWildcard();

    List<AggregationOperation> extractAggregations(final ProjectionMetadata metadata, final String key, final Object value);

    List<AggregationOperation> extractAggregations(final ProjectionMetadata metadata, final String compositePropertyWithFather, String key, final Object value);

    List<Criteria> extractCriteria(final ProjectionMetadata metadata, final String key, final Object value);

    List<Criteria> extractCriteria(final ProjectionMetadata metadata, final String compositePropertyWithFather, final String key, final Object value);



    boolean isTypeArray();

    public static Operator3[] values() {
        return new Operator3[]{
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

    public static Operator3 from(String value) {
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

    public static boolean containsOperator(final String value) {
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

    public static boolean isOperator(final String value) {
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
