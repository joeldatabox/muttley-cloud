package br.com.muttley.security.infra.controller;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.PasswdPayload;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.resource.UserResource;
import br.com.muttley.security.feign.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
public class UserManagerController {

    protected UserServiceClient service;


    @Autowired
    public UserManagerController(final UserServiceClient service) {
        this.service = service;
    }

    @RequestMapping(value = "${muttley.security.jwt.controller.managerUserEndPoint}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity update(@RequestBody User user, final @RequestHeader("${muttley.security.jwt.controller.tokenHeader}") String tokenHeader) {
        return ResponseEntity.ok(service.update(user.getUserName(), tokenHeader, user));
    }



    @RequestMapping(value = "${muttley.security.jwt.controller.managerUserEndPoint}", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserResource get(final @RequestHeader("${muttley.security.jwt.controller.tokenHeader}") String tokenHeader) {
        return new UserResource(this.service.getUserFromToken(new JwtToken(tokenHeader)));
    }

    @RequestMapping(value = "${muttley.security.jwt.controller.managerUserEndPoint}/password", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity updatePasswd(@RequestBody @Valid PasswdPayload passwdPayload, final @RequestHeader("${muttley.security.jwt.controller.tokenHeader}") String tokenHeader) {
        passwdPayload.setToken(new JwtToken(tokenHeader));
        service.updatePasswd(passwdPayload);
        return ResponseEntity.ok().build();

    }

}
