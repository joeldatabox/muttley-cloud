package br.com.muttley.security.server.controller;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserDataBinding;
import br.com.muttley.rest.RestResource;
import br.com.muttley.security.server.service.UserDataBindingService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Joel Rodrigues Moreira 12/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/users-databinding", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class UserDataBindingController implements RestResource {

    private final UserDataBindingService service;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public UserDataBindingController(final UserDataBindingService service, final UserService userService, final ApplicationEventPublisher eventPublisher) {
        this.service = service;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @RequestMapping(method = POST, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity save(
            @RequestBody final UserDataBinding value,
            final HttpServletResponse response,
            @RequestParam(required = false, value = "returnEntity", defaultValue = "") final String returnEntity,
            @RequestHeader(value = "${muttley.security.jwt.controller.tokenHeader-jwt}", defaultValue = "") final String tokenHeader) {

        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        final UserDataBinding record = service.save(user, value);

        publishCreateResourceEvent(this.eventPublisher, response, record);

        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/{id}", method = PUT, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity update(
            @PathVariable("id") final String id,
            @RequestBody final UserDataBinding model,
            @RequestHeader(value = "${muttley.security.jwt.controller.tokenHeader-jwt}", defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        //checkRoleUpdate(user);
        model.setId(id);
        return ResponseEntity.ok(service.update(user, model));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity list(final HttpServletResponse response, @RequestParam final Map<String, String> allRequestParams,
                               @RequestHeader(value = "${muttley.security.jwt.controller.tokenHeader-jwt}", defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        //checkRoleRead(user);
        return ResponseEntity.ok(this.service.listByUserName(user, user.getUserName()));
    }

    @RequestMapping(value = "/by-username/{userName}", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity listByUserName(@RequestHeader(value = "${muttley.security.jwt.controller.tokenHeader-jwt}", defaultValue = "") final String tokenHeader, @PathVariable("userName") final String userName) {
        return ResponseEntity.ok(this.service.listByUserName(userService.getUserFromToken(new JwtToken(tokenHeader)), userName));
    }

    @RequestMapping(value = "/by-username/{userName}", method = POST, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity saveByUserName(
            final HttpServletResponse response,
            @RequestParam(required = false, value = "returnEntity", defaultValue = "") final String returnEntity,
            @RequestHeader(value = "${muttley.security.jwt.controller.tokenHeader-jwt}", defaultValue = "") final String tokenHeader,
            @PathVariable("userName") final String userName,
            @RequestBody final UserDataBinding model) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        final UserDataBinding record = service.saveByUserName(user, userName, model);

        publishCreateResourceEvent(this.eventPublisher, response, record);

        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/by-username/{userName}", method = PUT, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity updateByUserName(
            @RequestHeader(value = "${muttley.security.jwt.controller.tokenHeader-jwt}", defaultValue = "") final String tokenHeader,
            @PathVariable("userName") final String userName,
            @RequestBody final UserDataBinding model) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        return ResponseEntity.ok(service.updateByUserName(user, userName, model));
    }

    @RequestMapping(value = "/merge/{userName}", method = PUT, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity merger(
            @RequestHeader(value = "${muttley.security.jwt.controller.tokenHeader-jwt}", defaultValue = "") final String tokenHeader,
            @PathVariable("userName") final String userName,
            @RequestBody final UserDataBinding model) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        service.merge(user, userName, model);
        return ResponseEntity.ok().build();
    }


}
