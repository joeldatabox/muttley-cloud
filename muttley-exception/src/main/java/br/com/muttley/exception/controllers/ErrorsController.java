package br.com.muttley.exception.controllers;

import br.com.muttley.exception.ErrorMessageBuilder;
import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Joel Rodrigues Moreira on 19/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Controller
@RequestMapping
public class ErrorsController {

    @RequestMapping(value = "/404", method = GET)
    public ResponseEntity noHandlerFoundException(@Autowired final ErrorMessageBuilder messageBuilder) {
        return messageBuilder.buildMessage(
                new MuttleyNotFoundException(null, null, null)
                        .setMessage("Endpoint inexistente =(")
        ).toResponseEntity();
    }

    @RequestMapping(value = "/500", method = GET)
    public ResponseEntity noInternalServerError(@Autowired final ErrorMessageBuilder messageBuilder) {
        return messageBuilder.buildMessage(
                new MuttleyException()
                        .setMessage("Desculpe pela vergonha =(")
        ).toResponseEntity();
    }
}
