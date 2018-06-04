package br.com.muttley.exception.service;

import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUserNameOrPasswordInvalidException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ResourceNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * @author Joel Rodrigues Moreira on 04/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Order(HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private final ErrorMessageBuilder messageBuilder;

    @Autowired
    public CustomResponseEntityExceptionHandler(final ErrorMessageBuilder messageBuilder) {
        this.messageBuilder = messageBuilder;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return messageBuilder.buildMessage(ex).toResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return messageBuilder.buildMessage(ex).toResponseEntity();
    }


    @Override
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return messageBuilder.buildMessage(ex).toResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return messageBuilder.buildMessage(ex).toResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return messageBuilder.buildMessage(ex).toResponseEntity();
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex, final WebRequest request) {
        return messageBuilder.buildMessage(ex).toResponseEntity();
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(final ConstraintViolationException ex) {
        return messageBuilder.buildMessage(ex).toResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return messageBuilder.buildMessage(new MuttleyNotFoundException(ex)).toResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return messageBuilder.buildMessage(ex).toResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return messageBuilder.buildMessage(ex).toResponseEntity();
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return messageBuilder.buildMessage(ex).toResponseEntity();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity handleResourceNotFoundException(final ResourceNotFoundException ex) {
        return messageBuilder.buildMessage(new MuttleyNotFoundException(ex)).toResponseEntity();
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity handleNullPointerException(final NullPointerException ex) {
        return messageBuilder.buildMessage(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }

    @ExceptionHandler(value = SerializationException.class)
    public ResponseEntity handleSerializationException(final SerializationException ex) {
        return messageBuilder.buildMessage(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleException(final Exception ex) {
        if (ex instanceof MuttleyException) {
            return handleMuttleyException((MuttleyException) ex);
        }
        return messageBuilder.buildMessage(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity exceptionRuntime(final RuntimeException ex) {
        if (ex instanceof MuttleyException) {
            return handleMuttleyException((MuttleyException) ex);
        }
        return messageBuilder.buildMessage(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }

    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity exceptionThrowable(final Throwable ex) {
        if (ex instanceof MuttleyException) {
            return handleMuttleyException((MuttleyException) ex);
        }
        return messageBuilder.buildMessage(new MuttleyException("ERROR *-*", ex)).toResponseEntity();
    }


    @ExceptionHandler(MuttleyException.class)
    public ResponseEntity handleMuttleyException(final MuttleyException ex) {
        return messageBuilder.buildMessage(ex).toResponseEntity();
    }

    @ExceptionHandler(MuttleyRepositoryException.class)
    public ResponseEntity handleMuttleyRepositoryException(final MuttleyRepositoryException ex) {
        return messageBuilder.buildMessage(ex).toResponseEntity();
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity handleUsernameNotFoundException(final UsernameNotFoundException ex) {
        final MuttleySecurityUserNameOrPasswordInvalidException exx = new MuttleySecurityUserNameOrPasswordInvalidException();
        exx.addSuppressed(ex);
        return handleMuttleySecurityUserNameOrPasswordInvalidException(exx);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity handleAccessDeniedException(final AccessDeniedException ex) {
        final MuttleySecurityUnauthorizedException exx = new MuttleySecurityUnauthorizedException().setStatus(FORBIDDEN);
        exx.addSuppressed(ex);
        return handleMuttleySecurityUnauthorizedException(exx);
    }

    @ExceptionHandler(value = MuttleySecurityUserNameOrPasswordInvalidException.class)
    public ResponseEntity handleMuttleySecurityUserNameOrPasswordInvalidException(final MuttleySecurityUserNameOrPasswordInvalidException ex) {
        return messageBuilder.buildMessage(ex).toResponseEntity();
    }

    @ExceptionHandler(value = MuttleySecurityUnauthorizedException.class)
    public ResponseEntity handleMuttleySecurityUnauthorizedException(final MuttleySecurityUnauthorizedException ex) {
        return messageBuilder.buildMessage(ex).toResponseEntity();
    }
}
