package br.com.muttley.mongo.infra.test.url.paramvalue;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.muttley.mongo.infra.test.url.paramvalue.QueryParamType.MULT;

/**
 * @author Joel Rodrigues Moreira on 24/10/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class QueryParamMultValue<T extends List<QueryParamValue<?>>> extends QueryParamValue<T> {

    public T getValue() {
        return this.value;
    }

    public QueryParamMultValue setValue(T values) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        if (value == null) {
            return "null";
        }
        return "[" + value.stream().map((final QueryParamValue<?> v) -> {
            if (v.value == null) {
                return "null";
            }
            return v.toString();
        }).collect(Collectors.joining(",")) + "]";
    }

    @Override
    public QueryParamType getType() {
        return MULT;
    }

    @Override
    public boolean isArray() {
        return true;
    }
}
