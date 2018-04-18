package br.com.muttley.security.server.controller;

import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.model.security.User;
import br.com.muttley.rest.RestController;
import br.com.muttley.rest.hateoas.resource.PageableResource;
import br.com.muttley.security.infra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

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
public class UserController implements RestController<User, String> {

    private UserService service;

    @Autowired
    public UserController(final UserService service) {
        this.service = service;
    }

    @Override
    @RequestMapping(method = POST, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(CREATED)
    public ResponseEntity save(@RequestBody final User value, final HttpServletResponse response, @RequestParam(required = false, value = "returnEntity", defaultValue = "") final String returnEntity) {
        final User record = service.save(value);
        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @RequestMapping(value = "/{email}", method = PUT, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity update(@PathVariable("email") final String email, @RequestBody final User user) {
        user.setEmail(email);
        return ResponseEntity.ok(service.update(user));
    }

    /**
     * Faz a deleção por email ao invez de ID
     */
    @Override
    @RequestMapping(value = "/{email}", method = DELETE, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity deleteById(@PathVariable("email") final String email) {
        service.removeByEmail(email);
        return ResponseEntity.ok().build();
    }

    /**
     * Faz a pesquisa pelo email ao invez do ID
     */
    @Override
    @RequestMapping(value = "/{email}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> findById(@PathVariable("email") final String email, final HttpServletResponse response) {
        return ResponseEntity.ok(service.findByEmail(email));
    }

    @Override
    public ResponseEntity first(final HttpServletResponse response) {
        throw new MuttleyException("Not Implemented", HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity loadHistoric(final String id, final HttpServletResponse response) {
        throw new MuttleyException("Not Implemented", HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<PageableResource> list(final HttpServletResponse response, final Map<String, String> allRequestParams) {
        throw new MuttleyException("Not Implemented", HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<String> count(final Map<String, Object> allRequestParams) {
        throw new MuttleyException("Not Implemented", HttpStatus.NOT_IMPLEMENTED);
    }
}
