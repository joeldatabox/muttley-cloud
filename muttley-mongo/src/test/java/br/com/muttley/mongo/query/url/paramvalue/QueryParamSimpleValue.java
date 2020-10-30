package br.com.muttley.mongo.query.url.paramvalue;

import static br.com.muttley.mongo.query.url.paramvalue.QueryParamType.SIMPLE;

/**
 * @author Joel Rodrigues Moreira on 24/10/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class QueryParamSimpleValue extends QueryParamValue<String> {

    public String getValue() {
        return this.value;
    }

    public QueryParamSimpleValue setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public QueryParamType getType() {
        return SIMPLE;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean isArray() {
        return false;
    }
}
