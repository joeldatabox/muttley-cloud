package br.com.muttley.security.server.security.controller;

import br.com.muttley.security.infra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@RestController
public class CreateUserController extends br.com.muttley.security.infra.controller.CreateUserController {
    @Autowired
    public CreateUserController(final ApplicationEventPublisher eventPublisher, final UserService service) {
        super(eventPublisher, service);
    }
}
