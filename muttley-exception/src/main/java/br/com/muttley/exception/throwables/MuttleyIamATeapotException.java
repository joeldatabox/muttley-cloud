package br.com.muttley.exception.throwables;

import br.com.muttley.exception.service.ErrorMessage;
import org.springframework.http.HttpStatus;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */

public class MuttleyIamATeapotException extends MuttleyException {

    public MuttleyIamATeapotException(final Throwable cause) {
        super(cause);
        setStatus(HttpStatus.I_AM_A_TEAPOT);
    }

    public MuttleyIamATeapotException(final Class clazz, final String field, final String message) {
        super(message, HttpStatus.I_AM_A_TEAPOT, clazz, field, message);
    }

    public MuttleyIamATeapotException(final String message) {
        super(message, HttpStatus.I_AM_A_TEAPOT);
    }

    public MuttleyIamATeapotException(final ErrorMessage errorMessage) {
        super(errorMessage);
        setStatus(HttpStatus.I_AM_A_TEAPOT);
    }
}
