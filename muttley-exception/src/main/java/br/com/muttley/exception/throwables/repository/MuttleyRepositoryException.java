package br.com.muttley.exception.throwables.repository;

import br.com.muttley.exception.throwables.MuttleyException;
import org.springframework.http.HttpStatus;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
public class MuttleyRepositoryException extends MuttleyException {

    public MuttleyRepositoryException(final Class clazz, final String field, final String message) {
        super("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, clazz, field, message);
        /*this.message = "Conflict";
        this.status = HttpStatus.CONFLICT;
        this.objectName = clazz.getSimpleName().toLowerCase();
        this.details.put(this.objectName + "." + field, message);*/
    }
}
