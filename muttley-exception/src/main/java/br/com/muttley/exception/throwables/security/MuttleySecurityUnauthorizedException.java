package br.com.muttley.exception.throwables.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
public class MuttleySecurityUnauthorizedException extends RuntimeException {

    protected HttpStatus status;
    final protected String message;
    protected String objectName;
    final protected Map<String, Object> details = new HashMap<>();

    public MuttleySecurityUnauthorizedException() {
        this.status = HttpStatus.UNAUTHORIZED;
        this.message = "ERROR *-*";
        this.objectName = "unknow :(";
    }

    public MuttleySecurityUnauthorizedException(final String message, final HttpStatus status, final Class clazz, final String field, final String info) {
        this.message = message;
        this.status = status;
        if (clazz != null) {
            this.objectName = clazz.getSimpleName().toLowerCase();
        }
        if (field != null) {
            if (this.objectName == null) {
                this.details.put(field, info);
            } else {
                this.details.put(this.objectName + "." + field, info);
            }
        }
    }

    public MuttleySecurityUnauthorizedException(final String message) {
        this(message, HttpStatus.UNAUTHORIZED);
    }

    public MuttleySecurityUnauthorizedException(final String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.objectName = "unknow :(";
        this.status = HttpStatus.UNAUTHORIZED;
    }

    public MuttleySecurityUnauthorizedException(final String message, final HttpStatus status) {
        super(message);
        this.message = message;
        this.objectName = "unknow :(";
        this.status = status;
    }

    public MuttleySecurityUnauthorizedException(final Throwable cause) {
        super(cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = "ERROR *-*";
        this.objectName = "unknow :(";
    }

    public MuttleySecurityUnauthorizedException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message;
        this.objectName = "unknow :(";
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public MuttleySecurityUnauthorizedException setStatus(final HttpStatus status) {
        this.status = status;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getObjectName() {
        return objectName;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public MuttleySecurityUnauthorizedException addDetails(final String key, final Object value) {
        this.details.put(key, value);
        return this;
    }

    public MuttleySecurityUnauthorizedException addDetails(final String key, final List<Object> value) {
        this.details.put(key, value);
        return this;
    }

    public MuttleySecurityUnauthorizedException addDetails(final Map<String, Object> details) {
        this.details.putAll(details);
        return this;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map getMapResponse() {
        final Map<String, Object> map = new HashMap<>();
        map.put("status", status.value());
        map.put("message", message);
        if (objectName != null) {
            map.put("objectName", objectName);
        }
        map.put("details", getDetails());
        return map;
    }

    public String toJson() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(getMapResponse());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public boolean containsDetais() {
        return !this.details.isEmpty();
    }
}
