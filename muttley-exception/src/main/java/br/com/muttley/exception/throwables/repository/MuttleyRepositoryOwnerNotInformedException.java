package br.com.muttley.exception.throwables.repository;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */

public class MuttleyRepositoryOwnerNotInformedException extends MuttleyRepositoryException {

    public MuttleyRepositoryOwnerNotInformedException(final Class clazz) {
        this(clazz, "Atenção, não foi informado o \"owner\" do objeto");
    }

    public MuttleyRepositoryOwnerNotInformedException(final Class clazz, final String message) {
        this(clazz, "owner", message);
    }

    public MuttleyRepositoryOwnerNotInformedException(final Class clazz, final String field, final String message) {
        super(clazz, field, message);
    }
}
