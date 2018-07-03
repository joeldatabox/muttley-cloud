package br.com.muttley.exception.throwables.repository;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */

public class MuttleyRepositoryIdIsNullException extends MuttleyRepositoryInvalidIdException {

    public MuttleyRepositoryIdIsNullException(final Class clazz) {
        this(clazz, "Atenção, o \"id\" do objeto está nullo");
    }

    public MuttleyRepositoryIdIsNullException(final Class clazz, final String message) {
        this(clazz, "id", message);
    }

    public MuttleyRepositoryIdIsNullException(final Class clazz, final String field, final String message) {
        super(clazz, field, message);
    }
}
