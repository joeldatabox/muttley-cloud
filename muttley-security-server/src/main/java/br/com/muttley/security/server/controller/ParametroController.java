package br.com.muttley.security.server.controller;

import br.com.muttley.model.parametrizacao.Parametro;
import br.com.muttley.rest.AbstractModelSyncRestController;
import br.com.muttley.security.server.service.AuthService;
import br.com.muttley.security.server.service.ParametroService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Carolina Cedro
 * @since 14/05/2025
 */
@RestController
@RequestMapping(value = "/api/v1/parametros", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class ParametroController extends AbstractRestController<Parametro> {

    private final ParametroService service;

    @Autowired
    public ParametroController(
            final ParametroService service,
            final UserService userService,
            final ApplicationEventPublisher eventPublisher
    ) {
        super(service, userService, eventPublisher);
        this.service = service;
    }


}
