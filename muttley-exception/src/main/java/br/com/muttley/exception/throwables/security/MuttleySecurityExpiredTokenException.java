package br.com.muttley.exception.throwables.security;

import org.springframework.http.HttpStatus;

/**
 * @author Carolina Cedro on 08/10/2024.
 * e-mail: <a href="mailto:ana.carolina@maxxsoft.com.br">ana.carolina@maxxsoft.com.br</a>
 * @project spring-cloud
 */
public class MuttleySecurityExpiredTokenException extends MuttleySecurityUnauthorizedException {

    public MuttleySecurityExpiredTokenException(final Class<?> clazz, final String field, final String message) {
        super(message, HttpStatus.BAD_REQUEST, clazz, field,
                "O token de redefinição de senha expirou ou é inválido. ");
    }
}
