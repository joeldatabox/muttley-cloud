package br.com.muttley.security.server.controller;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBase;
import br.com.muttley.model.security.UserPayLoad;
import br.com.muttley.security.server.service.UserBaseService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Joel Rodrigues Moreira on 09/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/users-base", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class UserBaseController extends AbstractRestController<UserBase> {

    private final UserBaseService service;

    @Autowired
    public UserBaseController(final UserBaseService service, final UserService userService, final ApplicationEventPublisher eventPublisher) {
        super(service, userService, eventPublisher);
        this.service = service;
    }

    @RequestMapping(value = "/userNamesIsAvaliable", method = RequestMethod.GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity userNameIsAvaliable(@RequestHeader(value = "${muttley.security.jwt.controller.tokenHeader-jwt}", defaultValue = "") final String tokenHeader, @RequestParam(value = "userNames") final Set<String> userNames) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        return ResponseEntity.ok(this.service.userNameIsAvaliable(user, userNames));
    }

    @RequestMapping(value = "/userByEmailOrUserName", method = RequestMethod.GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity userNameIsAvaliable(@RequestHeader(value = "${muttley.security.jwt.controller.tokenHeader-jwt}", defaultValue = "") final String tokenHeader, @RequestParam(value = "emailOrUsername") final String emailOrUsername) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        return ResponseEntity.ok(this.service.findUserByEmailOrUserNameOrNickUser(user, emailOrUsername));
    }

    @RequestMapping(value = "/create-new-user-and-add", method = RequestMethod.POST, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity createNewUserAndAdd(@RequestHeader(value = "${muttley.security.jwt.controller.tokenHeader-jwt}", defaultValue = "") final String tokenHeader, @RequestBody final UserPayLoad payLoad) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        this.service.createNewUserAndAdd(user, payLoad);
        return ResponseEntity.ok().build();
    }

}
