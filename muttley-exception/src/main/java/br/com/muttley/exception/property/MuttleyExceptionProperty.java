package br.com.muttley.exception.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Joel Rodrigues Moreira on 18/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@ConfigurationProperties(prefix = MuttleyExceptionProperty.PREFIX)
public class MuttleyExceptionProperty {
    protected static final String PREFIX = "muttley.exception.print";

    private boolean stackTrace = false;
    private boolean responseException = false;

    public boolean isStackTrace() {
        return stackTrace;
    }

    public MuttleyExceptionProperty setStackTrace(boolean stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }

    public boolean isResponseException() {
        return responseException;
    }

    public MuttleyExceptionProperty setResponseException(boolean responseException) {
        this.responseException = responseException;
        return this;
    }
}
