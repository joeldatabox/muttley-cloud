package br.com.muttley.exception.throwables;

import br.com.muttley.exception.ErrorMessage;
import org.springframework.http.HttpStatus;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */

public class MuttleyMethodNotAllowedException extends MuttleyException {


    public MuttleyMethodNotAllowedException(final String message) {
        super(message, HttpStatus.METHOD_NOT_ALLOWED);
    }

    public MuttleyMethodNotAllowedException(final Class clazz, final String field, final String message) {
        super("Method not allowed", HttpStatus.METHOD_NOT_ALLOWED, clazz, field, message);
    }

    public MuttleyMethodNotAllowedException(final ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
