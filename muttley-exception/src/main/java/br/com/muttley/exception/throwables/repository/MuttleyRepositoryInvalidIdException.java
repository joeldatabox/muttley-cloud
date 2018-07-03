package br.com.muttley.exception.throwables.repository;

/**
 * @author Joel Rodrigues Moreira on 03/07/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */

public class MuttleyRepositoryInvalidIdException extends MuttleyRepositoryException {

    public MuttleyRepositoryInvalidIdException(final Class clazz) {
        this(clazz, "Atenção, o \"id\" do objeto é inválido");
    }

    public MuttleyRepositoryInvalidIdException(final Class clazz, final String message) {
        this(clazz, "id", message);
    }

    public MuttleyRepositoryInvalidIdException(final Class clazz, final String field, final String message) {
        super(clazz, field, message);
    }
}
