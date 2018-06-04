package br.com.muttley.exception.throwables;

import br.com.muttley.exception.service.ErrorMessage;
import org.springframework.http.HttpStatus;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */

public class MuttleyNotAcceptableException extends MuttleyException {

    public MuttleyNotAcceptableException(final Class clazz, final String field, final String message) {
        super("Not Acceptable", HttpStatus.NOT_ACCEPTABLE, clazz, field, message);
    }

    public MuttleyNotAcceptableException(final ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
