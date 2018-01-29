package br.com.muttley.exception.throwables;

import org.springframework.http.HttpStatus;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */

public class MuttleyPageableRequestException extends MuttleyException {

    public MuttleyPageableRequestException() {
        this("invalid params of pagination");
    }

    public MuttleyPageableRequestException(final String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
