package br.com.muttley.exception.throwables;


import br.com.muttley.exception.ErrorMessage;

import static org.springframework.http.HttpStatus.I_AM_A_TEAPOT;

/**
 * @author Joel Rodrigues Moreira on 25/06/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MuttleyIamATeapotException extends MuttleyException {

    public MuttleyIamATeapotException(final Throwable cause) {
        super(cause);
        setStatus(I_AM_A_TEAPOT);
    }

    public MuttleyIamATeapotException(final Class clazz, final String field, final String message) {
        super(message, I_AM_A_TEAPOT, clazz, field, message);
    }

    public MuttleyIamATeapotException(final String message) {
        super(message, I_AM_A_TEAPOT);
    }

    public MuttleyIamATeapotException(final ErrorMessage errorMessage) {
        super(errorMessage);
        setStatus(I_AM_A_TEAPOT);
    }
}
