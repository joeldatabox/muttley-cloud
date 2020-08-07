package br.com.muttley.exception.throwables;

import br.com.muttley.exception.ErrorMessage;
import br.com.muttley.exception.service.event.MuttleyExceptionEvent;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

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
    protected Map<String, List<String>> headers = new HashMap<>();
    protected String field;
    protected List<MuttleyExceptionEvent> events = new ArrayList<>();

    public MuttleyException() {
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = "ERROR *-*";
        this.objectName = "unknow :(";
        this.field = null;
    }

    public MuttleyException(final String message, final HttpStatus status, final Class clazz, final String field, final String info) {
        this.message = message;
        this.status = status;
        if (clazz != null) {
            final String simpleName = clazz.getSimpleName();
            this.objectName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1, simpleName.length());
        }
        this.field = field;
        if (field != null) {
            this.details.put((this.objectName == null ? "" : this.objectName) + "." + field, info);
        }
    }

    public MuttleyException(final String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR);
        this.field = null;
    }

    public MuttleyException(final String message, final Throwable cause) {
        super(message, cause);
        this.message = message;
        this.objectName = "unknow :(";
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.field = null;
    }

    public MuttleyException(final String message, final HttpStatus status) {
        super(message);
        this.message = message;
        this.objectName = "unknow :(";
        this.status = status;
        this.field = null;
    }

    public MuttleyException(final Throwable cause) {
        super(cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = "ERROR *-*";
        this.objectName = "unknow :(";
        this.field = null;
    }

    public MuttleyException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message;
        this.objectName = "unknow :(";
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.field = null;
    }

    public MuttleyException(final ErrorMessage errorMessage) {
        this.status = errorMessage.getStatus();
        this.message = errorMessage.getMessage();
        this.objectName = errorMessage.getObjectName();
        this.field = errorMessage.getField();
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

    public String getField() {
        return field;
    }

    public MuttleyException setField(final String field) {
        this.field = field;
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

    public MuttleyException addDetails(final String key, final Object... value) {
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

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public MuttleyException setHeaders(final Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    public MuttleyException addHeaders(final String key, final String value) {
        if (this.headers.containsKey(key)) {
            this.headers.get(key).add(value);
        } else {
            final List<String> values = new ArrayList<>(1);
            values.add(value);
            this.headers.put(key, values);
        }
        return this;
    }

    public MuttleyException addHeaders(final String key, final String... value) {
        return this.addHeaders(key, asList(value));
    }

    public MuttleyException addHeaders(final String key, final List<String> value) {
        if (this.headers.containsKey(key)) {
            this.headers.get(key).addAll(value);
        } else {
            final List<String> values = new ArrayList<>(1);
            values.addAll(value);
            this.headers.put(key, values);
        }
        return this;
    }

    public MuttleyException addHeaders(final Map<String, List<String>> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public MuttleyException addEvent(final MuttleyExceptionEvent... events) {
        if (events != null) {
            this.getEvents().addAll(
                    asList(events)
                            .stream()
                            .filter(e -> e != null)
                            .collect(
                                    Collectors.toSet()
                            )
            );
        }
        return this;
    }

    public List<MuttleyExceptionEvent> getEvents() {
        return this.events;
    }


    public boolean containsDetais() {
        return !this.details.isEmpty();
    }
}
