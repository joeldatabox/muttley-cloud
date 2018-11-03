package br.com.muttley.metadata;

import lombok.Getter;

/**
 * @author Joel Rodrigues Moreira on 02/11/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Getter
public class RequestMetaDataMuttley {
    protected final String key;
    protected String currentValue;

    public RequestMetaDataMuttley(String key) {
        this.key = key;
    }

    public RequestMetaDataMuttley(String key, String currentValue) {
        this(key);
        this.currentValue = currentValue;
    }

    public boolean containsValidValue() {
        return this.currentValue != null;
    }
}
