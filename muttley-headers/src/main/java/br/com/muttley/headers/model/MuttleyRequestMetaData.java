package br.com.muttley.headers.model;

/**
 * @author Joel Rodrigues Moreira on 29/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MuttleyRequestMetaData {
    protected final String key;
    protected String currentValue;

    public MuttleyRequestMetaData(final String key) {
        this.key = key;
    }

    public MuttleyRequestMetaData(final String key, final String currentValue) {
        this(key);
        this.currentValue = currentValue;
    }

    public String getKey() {
        return key;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public boolean containsValidValue() {
        return this.getCurrentValue() != null;
    }
}
