package br.com.muttley.admin.server.security.controller;

import br.com.muttley.security.feign.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class CreateUserController extends br.com.muttley.security.infra.controller.CreateUserController {
    @Autowired
    public CreateUserController(final ApplicationEventPublisher eventPublisher, final UserServiceClient service) {
        super(eventPublisher, service);
    }
}

