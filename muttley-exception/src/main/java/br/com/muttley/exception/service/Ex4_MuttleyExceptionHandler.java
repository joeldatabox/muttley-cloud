package br.com.muttley.exception.service;

import br.com.muttley.exception.throwables.MuttleyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * A classe começa com o nome Ex4_ por conta de precedencia de exceptions do Spring
 *
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@ControllerAdvice
@RestController
public class Ex4_MuttleyExceptionHandler {

    private final ErrorMessageBuilder errorMessageBuilder;

    @Autowired
    public Ex4_MuttleyExceptionHandler(final ErrorMessageBuilder errorMessageBuilder) {
        this.errorMessageBuilder = errorMessageBuilder;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity exception(final Exception ex) {
        return errorMessageBuilder.build(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    public ResponseEntity httpMediaTypeNotSupportedException(final HttpMediaTypeNotSupportedException ex) {
        return errorMessageBuilder.build(ex).toResponseEntity();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity httpMessageNotReadableException(final HttpMessageNotReadableException ex) {
        return errorMessageBuilder.build(ex).toResponseEntity();
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity httpMessageNotReadableException(final HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity validationException(final MethodArgumentNotValidException ex) {
        return errorMessageBuilder.build(ex).toResponseEntity();
    }
}
