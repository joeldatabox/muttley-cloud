package br.com.muttley.mongo.infra;

public enum Operators {
    GTE(".$gte"),
    LT(".$lt"),
    IN(".$in") {
        @Override
        public boolean isSplit() {
            return true;
        }
    },
    CONTAINS(".$contains"),
    IS(".$is"),
    SKIP("$skip"),
    LIMIT("$limit"),
    OR(".$or") {
        @Override
        public boolean isSplit() {
            return true;
        }
    },
    ORDER_BY_ASC("$orderByAsc") {
        @Override
        public boolean isSplit() {
            return true;
        }
    },
    ORDER_BY_DESC("$orderByDesc") {
        @Override
        public boolean isSplit() {
            return true;
        }
    };

    private final String widcard;

    private Operators(String widcard) {
        this.widcard = widcard;
    }

    @Override
    public String toString() {
        return this.widcard;
    }

    public boolean isSplit() {
        return false;
    }

    public static Operators of(String value) {
        if (value.contains(".$id")) {
            value = value.replace(".$id", "");
        }
        if (value.contains(".$")) {
            value = value.substring(value.indexOf(".$"));
        } else if (value.contains("$")) {
            value = value.substring(value.indexOf("$"));
        }

        switch (value) {
            case ".$gte":
                return GTE;
            case ".$lt":
                return LT;
            case ".$in":
                return IN;
            case ".$contains":
                return CONTAINS;
            case ".$is":
                return IS;
            case "$skip":
                return SKIP;
            case "$limit":
                return LIMIT;
            case ".$or":
                return OR;
            case "$orderByAsc":
                return ORDER_BY_ASC;
            case "$orderByDesc":
                return ORDER_BY_DESC;
            default:
                return null;

        }
    }
}
