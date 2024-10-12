package br.com.muttley.exception.throwables.security;

import org.springframework.http.HttpStatus;

/**
 * @author Carolina Cedro on 08/10/2024.
 * e-mail: <a href="mailto:ana.carolina@maxxsoft.com.br">ana.carolina@maxxsoft.com.br</a>
 * @project spring-cloud
 */
public class MuttleySecurityEmailNotFoundtException extends MuttleySecurityUnauthorizedException {

    public MuttleySecurityEmailNotFoundtException(final Class clazz, final String field, final String message) {
        super(message, HttpStatus.BAD_REQUEST, clazz, field, "O email informado não foi encontrado. Verifique e tente novamente.");
    }

}
