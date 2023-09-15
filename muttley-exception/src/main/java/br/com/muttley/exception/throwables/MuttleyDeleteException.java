package br.com.muttley.exception.throwables;

import br.com.muttley.exception.service.ErrorMessage;

/**
 * @author Joel Rodrigues Moreira on 13/01/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MuttleyDeleteException extends MuttleyBadRequestException {

    public MuttleyDeleteException(Class clazz, String field, String message) {
        super(clazz, field, message);
    }

    public MuttleyDeleteException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
