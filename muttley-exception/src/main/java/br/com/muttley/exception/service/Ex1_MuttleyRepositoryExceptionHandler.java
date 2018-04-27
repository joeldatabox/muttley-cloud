package br.com.muttley.exception.service;

import br.com.muttley.exception.throwables.repository.MuttleyRepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * A classe começa com o nome Ex1_ por conta de precedencia de exceptions do Spring
 *
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@ControllerAdvice
@RestController
@Order(HIGHEST_PRECEDENCE)
public class Ex1_MuttleyRepositoryExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = MuttleyRepositoryException.class)
    public ResponseEntity SpringBootException(final MuttleyRepositoryException ex, @Autowired final ErrorMessageBuilder errorMessageBuilder) {
        return errorMessageBuilder.build(ex).toResponseEntity();
    }

}
