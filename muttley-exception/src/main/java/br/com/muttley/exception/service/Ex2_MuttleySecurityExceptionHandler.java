package br.com.muttley.exception.service;

import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUserNameOrPasswordInvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * A classe começa com o nome Ex2_ por conta de precedencia de exceptions do Spring
 *
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@ControllerAdvice
public class Ex2_MuttleySecurityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            MuttleySecurityUserNameOrPasswordInvalidException.class,
            MuttleySecurityUnauthorizedException.class
    })
    public ResponseEntity usernameNotFoundException(final Exception ex, @Autowired final ErrorMessageBuilder errorMessageBuilder) {
        if (ex instanceof UsernameNotFoundException) {
            final MuttleySecurityUserNameOrPasswordInvalidException secEx = new MuttleySecurityUserNameOrPasswordInvalidException();
            secEx.addSuppressed(ex);
            return errorMessageBuilder.build(secEx).toResponseEntity();
        } else if (ex instanceof MuttleySecurityUserNameOrPasswordInvalidException) {
            return errorMessageBuilder.build((MuttleySecurityUserNameOrPasswordInvalidException) ex).toResponseEntity();
        } else {
            return errorMessageBuilder.build((MuttleySecurityUnauthorizedException) ex).toResponseEntity();
        }
    }/*

    @ExceptionHandler(value = MuttleySecurityUserNameOrPasswordInvalidException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity usernameNotFoundException(final MuttleySecurityUserNameOrPasswordInvalidException ex) {
        return errorMessageBuilder.build(ex).toResponseEntity();
    }

    @ExceptionHandler(value = MuttleySecurityUnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity usernameNotFoundException(final MuttleySecurityUnauthorizedException ex) {
        return errorMessageBuilder.build(ex).toResponseEntity();
    }*/
}
