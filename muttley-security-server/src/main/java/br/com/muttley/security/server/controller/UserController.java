package br.com.muttley.security.server.controller;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.Passwd;
import br.com.muttley.model.security.User;
import br.com.muttley.security.server.service.UserService;
import br.com.muttley.security.server.service.impl.JwtTokenUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Joel Rodrigues Moreira on 17/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@org.springframework.web.bind.annotation.RestController
@RequestMapping(value = "/api/v1/users", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class UserController {

    private UserService service;

    @Autowired
    public UserController(final UserService service) {
        this.service = service;
    }

    @RequestMapping(method = POST, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(CREATED)
    public ResponseEntity save(@RequestBody final User value, final HttpServletResponse response, @RequestParam(required = false, value = "returnEntity", defaultValue = "") final String returnEntity) {
        final User record = service.save(value);
        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/{email}", method = PUT, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity update(@PathVariable("email") final String email, @RequestHeader("${muttley.security.jwt.controller.tokenHeader}") final String token, @RequestBody final User user, final JwtTokenUtilService tokenUtil) {
        if (isNullOrEmpty(token)) {
            throw new MuttleyBadRequestException(null, null, "informe um token válido");
        }

        final String emailFromToken = tokenUtil.getUsernameFromToken(token);

        if (isNullOrEmpty(emailFromToken)) {
            throw new MuttleyBadRequestException(null, null, "informe um token válido");
        }

        if (!emailFromToken.equals(email)) {
            throw new MuttleyBadRequestException(null, null, "O token informado não contem o email " + email);
        }
        user.setId(service.findByEmail(email).getId());
        return ResponseEntity.ok(service.update(user));
    }

    @RequestMapping(value = "/passwd", method = PUT, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity updatePasswd(@RequestBody final Passwd passwd) {
        service.updatePasswd(passwd);
        return ResponseEntity.ok().build();
    }

    /**
     * Faz a deleção por email ao invez de ID
     */
    @RequestMapping(value = "/{email}", method = DELETE, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity deleteByEmail(@PathVariable("email") final String email) {
        service.removeByEmail(email);
        return ResponseEntity.ok().build();
    }

    /**
     * Faz a pesquisa pelo email ao invez do ID
     */
    @RequestMapping(value = "/{email}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> findByEmail(@PathVariable("email") final String email, final HttpServletResponse response) {
        return ResponseEntity.ok(service.findByEmail(email));
    }

    @RequestMapping(value = "/user-from-token", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> getUserFromToken(@RequestBody final JwtToken token) {
        return ResponseEntity.ok(this.service.getUserFromToken(token));
    }

}
