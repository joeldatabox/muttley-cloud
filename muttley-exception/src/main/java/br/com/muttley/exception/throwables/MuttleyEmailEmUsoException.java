package br.com.muttley.exception.throwables;

import br.com.muttley.exception.service.ErrorMessage;

/**
 * @author Carolina Cedro on 10/10/2024.
 * e-mail: <a href="mailto:ana.carolina@maxxsoft.com.br">ana.carolina@maxxsoft.com.br</a>
 * @project muttley-cloud
 */
public class MuttleyEmailEmUsoException extends MuttleyBadRequestException {

    public MuttleyEmailEmUsoException(Class clazz, String field, String message) {
        super(clazz, field, message);
    }

    public MuttleyEmailEmUsoException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
