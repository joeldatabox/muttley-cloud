package br.com.muttley.exception;

import br.com.muttley.exception.property.MuttleyExceptionProperty;
import br.com.muttley.exception.service.event.MuttleyExceptionEvent;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Collection;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@Component
public class ErrorMessageBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ErrorMessageBuilder.class);
    private final ObjectMapper mapper;
    private final MuttleyExceptionProperty property;
    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    public ErrorMessageBuilder(final ObjectMapper mapper, final MuttleyExceptionProperty property) {
        this.mapper = mapper;
        this.property = property;
    }

    public ErrorMessage buildMessage(final MethodArgumentNotValidException ex) {
        final ErrorMessage message = new ErrorMessage()
                .setStatus(BAD_REQUEST)
                .setMessage(BAD_REQUEST.getReasonPhrase())
                .setObjectName(ex.getBindingResult().getObjectName());

        for (ObjectError fieldError : ex.getBindingResult().getAllErrors()) {
            String key = fieldError.getCodes()[0].replace(fieldError.getCodes()[fieldError.getCodes().length - 1] + ".", "");
            message.addDetails(key, fieldError.getDefaultMessage());
        }
        this.publishEvents(message);
        printException(ex, message);
        return message.setCustomMapper(mapper);
    }

    public ErrorMessage buildMessage(final ConstraintViolationException ex) {
        final ErrorMessage message = new ErrorMessage()
                .setStatus(BAD_REQUEST)
                .setMessage(BAD_REQUEST.getReasonPhrase());
        boolean cont = false;
        for (ConstraintViolation violation : ex.getConstraintViolations()) {
            if (!cont) {
                message.setObjectName(violation.getLeafBean().getClass().getSimpleName().toLowerCase());
                message.setMessage(violation.getMessage());
                cont = true;
            }
            String[] path = violation.getPropertyPath().toString().split("arg");
            String keyDetail = null;
            if (path.length > 1) {
                keyDetail = path[1].substring(path[1].indexOf(".") + 1);
            } else {
                keyDetail = path[0];
            }
            message.addDetails(
                    keyDetail.startsWith(message.objectName) ? keyDetail : message.objectName + "." + keyDetail, violation.getMessage()
            );
        }
        this.publishEvents(message);
        printException(ex, message);
        return message.setCustomMapper(mapper);
    }

    public ErrorMessage buildMessage(final BindException ex) {
        final ErrorMessage message = buildMessage(new MuttleyBadRequestException(ex));
        ex.getBindingResult().getFieldErrors().forEach(e -> {
            message.addDetails(e.getField(), e.getDefaultMessage());
        });
        this.publishEvents(message);
        printException(ex, message);
        return message.setCustomMapper(mapper);
    }

    public ErrorMessage buildMessage(final TypeMismatchException ex) {
        final ErrorMessage message = buildMessage(new MuttleyBadRequestException(ex));
        this.publishEvents(message);
        printException(ex, message);
        return message.setCustomMapper(mapper);
    }

    public ErrorMessage buildMessage(final MissingServletRequestPartException ex) {
        final ErrorMessage message = buildMessage(new MuttleyBadRequestException(ex));
        this.publishEvents(message);
        printException(ex, message);
        return message.setCustomMapper(mapper);
    }

    public ErrorMessage buildMessage(final MissingServletRequestParameterException ex) {
        final ErrorMessage message = buildMessage(new MuttleyBadRequestException(ex))
                .setMessage("Informe os parametros necessários")
                .addDetails("nameParam", ex.getParameterName());
        this.publishEvents(message);
        printException(ex, message);
        return message;
    }


    public ErrorMessage buildMessage(final MethodArgumentTypeMismatchException ex) {
        final ErrorMessage message = buildMessage(new MuttleyBadRequestException(ex));
        this.publishEvents(message);
        printException(ex, message);
        return message.setCustomMapper(mapper);
    }

    public ErrorMessage buildMessage(final HttpRequestMethodNotSupportedException ex) {
        final ErrorMessage message = buildMessage(new MuttleyBadRequestException(ex))
                .setStatus(METHOD_NOT_ALLOWED)
                .setMessage(METHOD_NOT_ALLOWED.getReasonPhrase());
        this.publishEvents(message);
        printException(ex, message);
        return message.setCustomMapper(mapper);
    }

    public ErrorMessage buildMessage(final HttpMediaTypeNotSupportedException ex) {
        final ErrorMessage message = new ErrorMessage()
                .setStatus(UNSUPPORTED_MEDIA_TYPE)
                .setMessage(ex.getMessage().replace("'null' ", ""))
                .addDetails("ContentType", ex.getContentType() == null ? "uninformed" : ex.getContentType().toString())
                .addDetails("SupportedMediaTypes", APPLICATION_JSON_VALUE);
        this.publishEvents(message);
        printException(ex, message);
        return message.setCustomMapper(mapper);
    }

    public ErrorMessage buildMessage(final HttpMessageNotReadableException ex) {
        final ErrorMessage message = new ErrorMessage()
                .setStatus(BAD_REQUEST);
        //procurando exceções de negocio
        final Throwable throwable = ex.getMostSpecificCause();

        if (throwable instanceof MuttleyException) {
            return buildMessage((MuttleyException) throwable);
        } else if (ex.getCause() instanceof com.fasterxml.jackson.core.JsonParseException) {
            final JsonLocation location = ((com.fasterxml.jackson.core.JsonParseException) ex.getCause()).getLocation();
            message.setMessage("Illegal character in line:" + location.getLineNr() + " column:" + location.getColumnNr());
        } else if (ex.getCause() instanceof JsonMappingException) {
            final JsonLocation location = ((JsonMappingException) ex.getCause()).getLocation();
            final String messageStr = ex.getMessage();

            if (messageStr.startsWith("JSON parse error: Can not deserialize instance of ")) {
                final String clazz = messageStr.replace("JSON parse error: Can not deserialize instance of ", "").replace(messageStr.substring(messageStr.indexOf(" out of START")), "");
                final String[] packageClazz = clazz.split("\\.");
                final String type = packageClazz[packageClazz.length - 1];
                final String typeStr = type.equals("ArrayList") || type.equals("HashSet") ? "Array" : type;
                final String typeSeted = messageStr.contains("START_OBJECT") ? "Object" : messageStr.contains("START_ARRAY") ? "Array" : "Unknown";
                message.setMessage("Era esperado um " + typeStr + " porem foi passado um " + typeSeted);
                if (location != null) {
                    message.addDetails("info", "Illegal character in line:" + location.getLineNr() + " column:" + location.getColumnNr());
                }
            } else {
                if (location != null) {
                    message.setMessage("Illegal character in line:" + location.getLineNr() + " column:" + location.getColumnNr());
                }
            }

        } else if (ex.getMessage().contains("Required request body is missing:")) {
            message.setMessage("Insira o corpo na requisição!")
                    .addDetails("body", "body is empty");
        }
        this.publishEvents(message);
        printException(ex, message);
        return message;
    }

    public ErrorMessage buildMessage(final MuttleyException ex) {
        final ErrorMessage message = new ErrorMessage()
                .setStatus(ex.getStatus())
                .setMessage(ex.getMessage())
                .setObjectName(ex.getObjectName())
                .addDetails(ex.getDetails())
                .addHeaders(ex.getHeaders());
        this.publishEvents(message);
        printException(ex, message);
        return message.setCustomMapper(mapper);
    }

    public ErrorMessage buildMessage(final MuttleyRepositoryException ex) {
        final ErrorMessage message = new ErrorMessage()
                .setStatus(ex.getStatus())
                .addHeaders(ex.getHeaders());
        this.publishEvents(message);
        printException(ex, message);
        return message.setCustomMapper(mapper);
    }

    public ErrorMessage buildMessage(final MuttleySecurityUnauthorizedException ex) {
        final ErrorMessage message = new ErrorMessage()
                .setStatus(ex.getStatus())
                .setMessage(ex.getMessage())
                .setObjectName(ex.getObjectName())
                .addDetails(ex.getDetails())
                .addHeaders(ex.getHeaders());
        this.publishEvents(message);
        printException(ex, message);
        return message.setCustomMapper(mapper);
    }

    private void publishEvents(final ErrorMessage message) {
        this.publishEvents(message, null);
    }

    private void publishEvents(final ErrorMessage message, final Collection<MuttleyExceptionEvent> events) {
        if (this.publisher != null) {
            this.publisher.publishEvent(new MuttleyExceptionEvent(message));
            if (!CollectionUtils.isEmpty(events)) {
                events.forEach(it -> this.publisher.publishEvent(it.setSource(message)));
            }
        }
    }

    /**
     * Imprime a pilha de Exceptions de acordo com application.properties
     *
     * @param ex ->exceção para ser logada!
     */
    private void printException(final Exception ex, final ErrorMessage message) {
        if (property != null && property.isStackTrace()) {
            ex.printStackTrace();
        }
        if (property != null && property.isResponseException()) {
            logger.info(message.toJsonPretty());
        }
    }

    /**
     * Verifica se na arvore de exceptions existe algum erro advindo de regra de negocio.
     * Caso exista esse erro é retornado primordialmente;
     *
     * @param exActual  ->Exception atual
     * @param exPrevius ->Exception que aponta para Exception atual
     */
    /*private Throwable findMuttleyException(final Throwable exActual, final Throwable exPrevius) {
        if (exActual == null || exActual.equals(exPrevius)) {
            return null;
        }
        if (exActual instanceof MuttleyException) {
            return exActual;
        } else {
            return findMuttleyException(exActual.getCause(), exActual);
        }

    }*/
}
