package br.com.muttley.exception.throwables.security;

import br.com.muttley.exception.ErrorMessage;
import br.com.muttley.exception.throwables.MuttleyException;
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
public class MuttleySecurityUnauthorizedException extends MuttleyException {

    public MuttleySecurityUnauthorizedException() {
        this.status = HttpStatus.UNAUTHORIZED;
        this.message = "ERROR *-*";
        this.objectName = "unknow :(";
    }

    public MuttleySecurityUnauthorizedException(final ErrorMessage errorMessage) {
        super(errorMessage);
        if (errorMessage.getMessage() == null) {
            this.message = "Usuário e/ou senha incorreto(s)";
        }
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

    @Override
    public MuttleySecurityUnauthorizedException setStatus(final HttpStatus status) {
        super.setStatus(status);
        return this;
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

    @Override
    public MuttleySecurityUnauthorizedException addDetails(final String key, final Object value) {
        super.addDetails(key, value);
        return this;
    }

    @Override
    public MuttleySecurityUnauthorizedException addDetails(final String key, final List<Object> value) {
        super.addDetails(key, value);
        return this;
    }

    @Override
    public MuttleySecurityUnauthorizedException addDetails(final Map<String, Object> details) {
        super.addDetails(details);
        return this;
    }

    public String toJson() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(getMapResponse());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
