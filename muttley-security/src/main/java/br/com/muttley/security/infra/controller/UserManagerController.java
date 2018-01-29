package br.com.muttley.security.infra.controller;

import br.com.muttley.model.security.model.Passwd;
import br.com.muttley.model.security.model.User;
import br.com.muttley.model.security.model.resource.UserResource;
import br.com.muttley.security.infra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
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

    protected UserService service;

    @Autowired
    public UserManagerController(UserService service) {
        this.service = service;
    }

    @RequestMapping(value = "${muttley.security.jwt.controller.managerUserEndPoint}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity update(@RequestBody User user) {
        user.setId(this.service.getCurrentUser().getId());
        User other = service.update(user);
        return ResponseEntity.ok(other);
    }

    @RequestMapping(value = "${muttley.security.jwt.controller.managerUserEndPoint}", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserResource get() {
        return new UserResource(this.service.getCurrentUser());
    }

    @RequestMapping(value = "${muttley.security.jwt.controller.managerUserEndPoint}/password", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity updatePasswd(@RequestBody @Valid Passwd passwd) {
        passwd.setId(this.service.getCurrentUser().getId());
        return ResponseEntity.ok().body(service.updatePasswd(passwd));
    }

}
