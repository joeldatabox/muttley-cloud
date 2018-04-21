package br.com.muttley.exception.service;

import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

/**
 * A classe começa com o nome Ex3_ por conta de precedencia de exceptions do Spring
 *
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@ControllerAdvice
public class Ex0_MuttleyCloudExceptionHandler extends ResponseEntityExceptionHandler {

   /* private final ErrorMessageBuilder errorMessageBuilder;

    @Autowired
    public Ex0_MuttleyCloudExceptionHandler(final ErrorMessageBuilder errorMessageBuilder) {
        this.errorMessageBuilder = errorMessageBuilder;
    }
*/
    @ExceptionHandler(value = {
            MuttleyException.class,
            ResourceNotFoundException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity SpringBootException(final Exception ex, @Autowired final ErrorMessageBuilder errorMessageBuilder) {
        if (ex instanceof MuttleyException) {
            return errorMessageBuilder.build((MuttleyException) ex).toResponseEntity();
        } else if (ex instanceof ResourceNotFoundException) {
            return errorMessageBuilder.build(new MuttleyNotFoundException((ResourceNotFoundException) ex)).toResponseEntity();
        } else if (ex instanceof NoHandlerFoundException) {
            return errorMessageBuilder.build(new MuttleyNotFoundException((NoHandlerFoundException) ex)).toResponseEntity();
        } else {
            return errorMessageBuilder.build((ConstraintViolationException) ex).toResponseEntity();
        }
    }/*

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity not(final ResourceNotFoundException ex) {
        return errorMessageBuilder.build(new MuttleyNotFoundException(ex)).toResponseEntity();
    }

    @ExceptionHandler(value = NoHandlerFoundException.class)
    public ResponseEntity not(final NoHandlerFoundException ex) {
        return errorMessageBuilder.build(new MuttleyNotFoundException(ex)).toResponseEntity();
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity validationException(final ConstraintViolationException ex) {
        return errorMessageBuilder.build(ex).toResponseEntity();
    }

*/
}
