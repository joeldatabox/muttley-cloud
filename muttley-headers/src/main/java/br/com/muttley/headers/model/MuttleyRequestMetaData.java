package br.com.muttley.headers.model;

import org.springframework.beans.factory.ObjectProvider;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Joel Rodrigues Moreira on 29/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MuttleyRequestMetaData {
    protected final String key;
    private final HttpServletRequest request;
    protected String currentValue;
    private boolean resolved = false;

    public MuttleyRequestMetaData(final String key, final HttpServletRequest request) {
        this.key = key;
        this.request = request;
    }

    public MuttleyRequestMetaData(final String key, final ObjectProvider<HttpServletRequest> requestProvider) {
        this(key, requestProvider.getIfAvailable());
    }

    public String getKey() {
        return key;
    }

    public String getCurrentValue() {
        if (!this.resolved) {
            this.resolved = true;
            if (request != null) {
                this.currentValue = request.getHeader(this.key);
            } else {
                this.currentValue = null;
            }
        }
        return this.currentValue;
    }

    public boolean containsValidValue() {
        return this.getCurrentValue() != null;
    }
}
