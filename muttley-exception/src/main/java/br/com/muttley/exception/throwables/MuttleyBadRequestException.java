package br.com.muttley.exception.throwables;

import br.com.muttley.exception.ErrorMessage;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */

public class MuttleyBadRequestException extends MuttleyException {
    public MuttleyBadRequestException() {
    }

    public MuttleyBadRequestException(final Throwable cause) {
        super(cause);
        super.setMessage("Bad Request")
                .setStatus(BAD_REQUEST)
                .setObjectName(null);
    }

    public MuttleyBadRequestException(final Class clazz, final String field, final String message) {
        super(message, BAD_REQUEST, clazz, field, message);
    }

    public MuttleyBadRequestException(final ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
