package br.com.muttley.exception.throwables;

import br.com.muttley.exception.service.ErrorMessage;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */

public class MuttleyException extends RuntimeException {
    protected HttpStatus status;
    protected String message;
    protected String objectName;
    protected Map<String, Object> details = new HashMap<>();

    public MuttleyException() {
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = "ERROR *-*";
        this.objectName = "unknow :(";
    }

    public MuttleyException(final String message, final HttpStatus status, final Class clazz, final String field, final String info) {
        this.message = message;
        this.status = status;
        if (clazz != null) {
            this.objectName = clazz.getSimpleName().toLowerCase();
        }
        if (field != null) {
            this.details.put(this.objectName + "." + field, info);
        }
    }

    public MuttleyException(final String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public MuttleyException(final String message, final Throwable cause) {
        super(message, cause);
        this.message = message;
        this.objectName = "unknow :(";
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public MuttleyException(final String message, final HttpStatus status) {
        super(message);
        this.message = message;
        this.objectName = "unknow :(";
        this.status = status;
    }

    public MuttleyException(final Throwable cause) {
        super(cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = "ERROR *-*";
        this.objectName = "unknow :(";
    }

    public MuttleyException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message;
        this.objectName = "unknow :(";
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public MuttleyException(final ErrorMessage errorMessage) {
        this.status = errorMessage.getStatus();
        this.message = errorMessage.getMessage();
        this.objectName = errorMessage.getObjectName();
        if (errorMessage.containsDetails()) {
            this.details.putAll(errorMessage.getDetails());
        }
    }

    public HttpStatus getStatus() {
        return status;
    }

    public MuttleyException setStatus(final HttpStatus status) {
        this.status = status;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public MuttleyException setMessage(final String message) {
        this.message = message;
        return this;
    }

    public String getObjectName() {
        return objectName;
    }

    public MuttleyException setObjectName(final String objectName) {
        this.objectName = objectName;
        return this;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public MuttleyException setDetails(final Map<String, Object> details) {
        this.details = details;
        return this;
    }

    public MuttleyException addDetails(final String key, final Object value) {
        this.details.put(key, value);
        return this;
    }

    public MuttleyException addDetails(final String key, final List<Object> value) {
        this.details.put(key, value);
        return this;
    }

    public MuttleyException addDetails(final Map<String, Object> details) {
        this.details.putAll(details);
        return this;
    }

    public boolean containsDetais() {
        return !this.details.isEmpty();
    }
}
