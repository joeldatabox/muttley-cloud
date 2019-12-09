package br.com.muttley.exception.throwables;

import br.com.muttley.exception.service.ErrorMessage;
import org.springframework.http.HttpStatus;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */

public class MuttleyNotFoundException extends MuttleyException {

    public MuttleyNotFoundException(final Throwable cause) {
        super(cause);
    }

    public MuttleyNotFoundException(final Class clazz, final String field, final String message) {
        super(message, HttpStatus.NOT_FOUND, clazz, field, message);
    }

    public MuttleyNotFoundException(final ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
