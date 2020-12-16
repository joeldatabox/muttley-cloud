package br.com.muttley.security.infra.controller;

import br.com.muttley.security.feign.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 16/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
public class UserNameIsAvaliableController {
    protected UserServiceClient service;

    @Autowired
    public UserNameIsAvaliableController(final UserServiceClient service) {
        this.service = service;
    }

    @RequestMapping(value = "${muttley.security.jwt.controller.usesrNameIsAvaliable}", method = RequestMethod.GET)
    public ResponseEntity userNameIsAvaliable(@RequestParam(value = "userNames") final Set<String> userNames) {
        return ResponseEntity.ok(this.service.userNameIsAvaliable(userNames));
    }
}
