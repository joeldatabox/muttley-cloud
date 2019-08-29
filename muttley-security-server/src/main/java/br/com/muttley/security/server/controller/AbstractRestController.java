package br.com.muttley.security.server.controller;

import br.com.muttley.domain.Service;
import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.rest.RestResource;
import br.com.muttley.rest.hateoas.resource.PageableResource;
import br.com.muttley.security.server.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
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
import java.util.Map;
import java.util.Set;

import static br.com.muttley.security.server.property.MuttleySecurityProperty.TOKEN_HEADER_JWT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractRestController<T extends Document> implements RestResource<T> {
    protected final Service<T> service;
    protected final UserService userService;
    protected final ApplicationEventPublisher eventPublisher;

    public AbstractRestController(final Service service, final UserService userService, final ApplicationEventPublisher eventPublisher) {
        this.service = service;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity save(
            @RequestBody final T value,
            final HttpServletResponse response,
            @RequestParam(required = false, value = "returnEntity", defaultValue = "") final String returnEntity,
            @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {

        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));

        final T record = service.save(user, value);

        publishCreateResourceEvent(this.eventPublisher, response, record);

        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity update(@PathVariable("id") final String id, @RequestBody final T model, @RequestHeader(value = "${muttley.security.jwt.controller.token-header-jwt}", defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));

        model.setId(id);
        return ResponseEntity.ok(service.update(user, model));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity deleteById(@PathVariable("id") final String id, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        service.deleteById(user, id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity findById(@PathVariable("id") final String id, final HttpServletResponse response, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        final T value = service.findById(user, id);
        publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(value);
    }

    @RequestMapping(value = "/ids", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity findByIds(@RequestParam(required = false, value = "ids") String[] ids, HttpServletResponse response, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final Set<T> value = service.findByIds(this.userService.getUserFromToken(new JwtToken(tokenHeader)), ids);

        publishSingleResourceRetrievedEvent(this.eventPublisher, response);

        return ResponseEntity.ok(value);
    }

    @RequestMapping(value = "/first", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity first(final HttpServletResponse response, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        final T value = service.findFirst(user);
        publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(value);
    }

    @RequestMapping(value = "/{id}/historic", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity loadHistoric(@PathVariable("id") final String id, final HttpServletResponse response, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        final Historic historic = service.loadHistoric(user, id);
        publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(historic);
    }

    @RequestMapping(method = GET)
    public ResponseEntity<PageableResource<T>> list(final HttpServletResponse response, @RequestParam final Map<String, String> allRequestParams,
                                                    @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        return ResponseEntity.ok(toPageableResource(eventPublisher, response, this.service, user, allRequestParams));
    }

    @RequestMapping(value = "/count", method = GET, produces = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity count(@RequestParam final Map<String, String> allRequestParams, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        return ResponseEntity.ok(String.valueOf(service.count(user, allRequestParams)));
    }


}
