package br.com.muttley.exception.service;

import br.com.muttley.exception.throwables.MuttleyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.serializer.SerializationException;
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

import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

/**
 * A classe começa com o nome Ex4_ por conta de precedencia de exceptions do Spring
 *
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@ControllerAdvice
@RestController
@Order(LOWEST_PRECEDENCE)
public class Ex3_MuttleyExceptionHandler {

    private final ErrorMessageBuilder errorMessageBuilder;

    @Autowired
    public Ex3_MuttleyExceptionHandler(final ErrorMessageBuilder errorMessageBuilder) {
        this.errorMessageBuilder = errorMessageBuilder;
    }

    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    public ResponseEntity httpMediaTypeNotSupportedException(final HttpMediaTypeNotSupportedException ex) {
        return errorMessageBuilder.build(ex).toResponseEntity();
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity httpMessageNotReadableException(final HttpMessageNotReadableException ex) {
        return errorMessageBuilder.build(ex).toResponseEntity();
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity httpMessageNotReadableException(final HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity validationException(final MethodArgumentNotValidException ex) {
        return errorMessageBuilder.build(ex).toResponseEntity();
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity exceptionNullPointerException(final NullPointerException ex) {
        return errorMessageBuilder.build(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = SerializationException.class)
    public ResponseEntity exceptionSerializationException(final SerializationException ex) {
        return errorMessageBuilder.build(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity exception(final Exception ex) {
        return errorMessageBuilder.build(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity exceptionRuntime(final RuntimeException ex) {
        return errorMessageBuilder.build(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity exceptionThrowable(final Throwable ex) {
        return errorMessageBuilder.build(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }
}
