package br.com.muttley.security.gateway.controller;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.Passwd;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.resource.UserResource;
import br.com.muttley.security.infra.feign.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static br.com.muttley.security.gateway.properties.MuttleySecurityProperties.MANAGER_USER_END_POINT;
import static br.com.muttley.security.gateway.properties.MuttleySecurityProperties.TOKEN_HEADER;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@RestController
public class UserManagerController {

    protected UserServiceClient service;

    @Autowired
    public UserManagerController(final UserServiceClient service) {
        this.service = service;
    }

    @RequestMapping(value = MANAGER_USER_END_POINT, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity update(@RequestBody User user, final @RequestHeader("${muttley.security.jwt.controller.tokenHeader}") String tokenHeader) {
        return ResponseEntity.ok(service.update(user.getUserName(), tokenHeader, user));
    }

    @RequestMapping(value = MANAGER_USER_END_POINT, method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserResource get(final @RequestHeader(TOKEN_HEADER) String tokenHeader) {
        return new UserResource(this.service.getUserFromToken(new JwtToken(tokenHeader)));
    }

    @RequestMapping(value = MANAGER_USER_END_POINT + "/password", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity updatePasswd(@RequestBody @Valid Passwd passwd, final @RequestHeader(TOKEN_HEADER) String tokenHeader) {
        passwd.setToken(new JwtToken(tokenHeader));
        service.updatePasswd(passwd);
        return ResponseEntity.ok().build();

    }

}
