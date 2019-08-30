package br.com.muttley.mongo.infra;

public enum Operator {
    GTE(".$gte"),
    LTE(".$lte"),
    GT(".$gt"),
    LT(".$lt"),
    IN(".$in") {
        @Override
        public boolean isRequiredArray() {
            return true;
        }
    },
    CONTAINS(".$contains"),
    IS(".$is"),
    SKIP("$skip"),
    LIMIT("$limit"),
    OR(".$or") {
        @Override
        public boolean isRequiredArray() {
            return true;
        }
    },
    ORDER_BY_ASC("$orderByAsc") {
        @Override
        public boolean isRequiredArray() {
            return true;
        }
    },
    ORDER_BY_DESC("$orderByDesc") {
        @Override
        public boolean isRequiredArray() {
            return true;
        }
    };

    private final String widcard;

    private Operator(String widcard) {
        this.widcard = widcard;
    }

    @Override
    public String toString() {
        return this.widcard;
    }

    public boolean isRequiredArray() {
        return false;
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
