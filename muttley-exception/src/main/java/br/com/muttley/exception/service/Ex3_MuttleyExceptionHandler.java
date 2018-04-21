package br.com.muttley.exception.service;

import br.com.muttley.exception.throwables.MuttleyException;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * A classe começa com o nome Ex4_ por conta de precedencia de exceptions do Spring
 *
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@ControllerAdvice
@RestController
public class Ex3_MuttleyExceptionHandler {

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(value = {
            HttpMediaTypeNotSupportedException.class,
            HttpMessageNotReadableException.class,
            HttpRequestMethodNotSupportedException.class,
            MethodArgumentNotValidException.class,
            NullPointerException.class,
            SerializationException.class,
            RuntimeException.class,
            Exception.class,
            Throwable.class
    })
    public ResponseEntity httpMediaTypeNotSupportedException(final Throwable ex, @Autowired final ErrorMessageBuilder errorMessageBuilder) {
        if (ex instanceof HttpMediaTypeNotSupportedException) {
            return errorMessageBuilder.build((HttpMediaTypeNotSupportedException) ex).toResponseEntity();
        } else if (ex instanceof HttpMessageNotReadableException) {
            return errorMessageBuilder.build((HttpMessageNotReadableException) ex).toResponseEntity();
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        } else if (ex instanceof MethodArgumentNotValidException) {
            return errorMessageBuilder.build((MethodArgumentNotValidException) ex).toResponseEntity();
        }
        return errorMessageBuilder.build(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }

    /*@ResponseStatus(HttpStatus.BAD_REQUEST)
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

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity exceptionNullPointerException(final NullPointerException ex) {
        return errorMessageBuilder.build(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = SerializationException.class)
    public ResponseEntity exceptionSerializationException(final SerializationException ex) {
        return errorMessageBuilder.build(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity exception(final Exception ex) {
        return errorMessageBuilder.build(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity exceptionRuntime(final RuntimeException ex) {
        return errorMessageBuilder.build(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity exceptionThrowable(final Throwable ex) {
        return errorMessageBuilder.build(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }*/
}
