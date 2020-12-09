package br.com.muttley.mongo.infra.newagregation.paramvalue;

import java.util.stream.Stream;

import static br.com.muttley.mongo.infra.newagregation.paramvalue.QueryParamType.MULT_SIMPLE;
import static java.util.stream.Collectors.joining;

/**
 * @author Joel Rodrigues Moreira on 24/10/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class QueryParamMultSimpleValue extends QueryParamValue<String[]> {

    public String[] getValue() {
        return this.value;
    }

    public QueryParamMultSimpleValue setValue(String[] value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        if (this.value == null) {
            return "null";
        }
        return "[" + Stream.of(this.value).collect(joining("|")) + "]";
    }

    @Override
    public QueryParamType getType() {
        return MULT_SIMPLE;
    }

    @Override
    public boolean isArray() {
        return true;
    }
}
