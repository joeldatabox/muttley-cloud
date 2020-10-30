package br.com.muttley.mongo.query.url.paramvalue;

/**
 * @author Joel Rodrigues Moreira on 24/10/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class QueryParamValue<T> {
    protected T value;

    public static QueryParamValue from(String value) {
        return new QueryParamSimpleValue().setValue(value);
    }

    abstract QueryParamType getType();

    public abstract boolean isArray();
}
