package br.com.muttley.mongo.infra.newagregation.paramvalue;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Joel Rodrigues Moreira on 29/10/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@EqualsAndHashCode(of = {"key", "value"})
public class QueryParamImpl implements QueryParam {

    private String key;
    private String value;

    protected QueryParamImpl(String key, String value) {
        this.key = key;
        this.value = value;
    }

    protected QueryParamImpl(String value) {
        if (value.endsWith("]")) {
            this.key = value.substring(0, value.indexOf("="));
            this.value = value.substring(this.key.length() + 2, value.length() - 1);
        } else {
            final String[] valueSplit = value.split("=");
            if (valueSplit.length < 2) {
                this.key = valueSplit[0];
                this.value = null;
            } else {
                this.key = valueSplit[0];
                this.value = valueSplit[1];
            }
        }
    }

    @Override
    public boolean isArrayValue() {
        return value.startsWith("[") && value.endsWith("]");
    }

    @Override
    public String toString() {
        return "NewQueryParam{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
