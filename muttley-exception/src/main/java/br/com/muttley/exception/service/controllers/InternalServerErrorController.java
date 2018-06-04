package br.com.muttley.exception.service.controllers;

import org.springframework.boot.autoconfigure.web.ErrorController;

/**
 * @author Joel Rodrigues Moreira on 04/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
//@Controller
public class InternalServerErrorController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
