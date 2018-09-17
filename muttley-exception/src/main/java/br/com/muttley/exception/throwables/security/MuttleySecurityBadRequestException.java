package br.com.muttley.exception.throwables.security;

import org.springframework.http.HttpStatus;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
public class MuttleySecurityBadRequestException extends MuttleySecurityUnauthorizedException {

    public MuttleySecurityBadRequestException(final Class clazz, final String field, final String message) {
        super(message, HttpStatus.BAD_REQUEST, clazz, field, "Bad Request");
    }
}
