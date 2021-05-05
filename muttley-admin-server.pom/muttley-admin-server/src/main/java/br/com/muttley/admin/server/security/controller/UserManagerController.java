package br.com.muttley.admin.server.security.controller;

import br.com.muttley.security.feign.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
public class UserManagerController extends br.com.muttley.security.infra.controller.UserManagerController {

    @Autowired
    public UserManagerController(UserServiceClient service) {
        super(service);
    }

}
