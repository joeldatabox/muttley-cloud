package br.com.muttley.exception.throwables;

import br.com.muttley.exception.service.ErrorMessage;
import org.springframework.http.HttpStatus;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */

public class MuttleyConflictException extends MuttleyException {

    public MuttleyConflictException(final Class clazz, final String field, final String message) {
        super(message, HttpStatus.CONFLICT, clazz, field, message);
        /*this.message = "Conflict";
        this.status = HttpStatus.CONFLICT;
        this.objectName = clazz.getSimpleName().toLowerCase();
        this.details.put(this.objectName + "." + field, message);*/
    }

    public MuttleyConflictException(final ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
