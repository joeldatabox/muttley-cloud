package br.com.muttley.exception.throwables.repository;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */

public class MuttleyRepositoryUserNotInformedException extends MuttleyRepositoryException {

    public MuttleyRepositoryUserNotInformedException(final Class clazz) {
        this(clazz, "Atenção, o \"usuario\" do objeto está nullo");
    }

    public MuttleyRepositoryUserNotInformedException(final Class clazz, final String message) {
        this(clazz, "user", message);
    }

    public MuttleyRepositoryUserNotInformedException(final Class clazz, final String field, final String message) {
        super(clazz, field, message);
    }
}
