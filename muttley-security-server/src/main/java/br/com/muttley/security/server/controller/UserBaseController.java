package br.com.muttley.security.server.controller;

import br.com.muttley.model.security.UserBase;
import br.com.muttley.security.server.service.UserBaseService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Joel Rodrigues Moreira on 09/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/users-base", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class UserBaseController extends AbstractRestController<UserBase> {

    @Autowired
    public UserBaseController(final UserBaseService service, final UserService userService, final ApplicationEventPublisher eventPublisher) {
        super(service, userService, eventPublisher);
    }


}
