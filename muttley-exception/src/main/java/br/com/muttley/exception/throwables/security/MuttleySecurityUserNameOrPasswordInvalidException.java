package br.com.muttley.exception.throwables.security;

import org.springframework.http.HttpStatus;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
public class MuttleySecurityUserNameOrPasswordInvalidException extends MuttleySecurityUnauthorizedException {

    public MuttleySecurityUserNameOrPasswordInvalidException() {
        this("Usuário e/ou senha incorreto(s)");
        this.status = HttpStatus.UNAUTHORIZED;
    }

    public MuttleySecurityUserNameOrPasswordInvalidException(final String message) {
        super(message);
        this.status = HttpStatus.UNAUTHORIZED;
    }
}
