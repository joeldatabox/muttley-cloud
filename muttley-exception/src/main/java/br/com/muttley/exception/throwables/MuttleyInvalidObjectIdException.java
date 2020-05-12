package br.com.muttley.exception.throwables;

/**
 * @author Joel Rodrigues Moreira on 11/05/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MuttleyInvalidObjectIdException extends MuttleyBadRequestException {

    public MuttleyInvalidObjectIdException() {
        this(null, null, "invalid id");
    }

    public MuttleyInvalidObjectIdException(final Class clazz, final String field, final String message) {
        super(clazz, field, message);
    }
}
