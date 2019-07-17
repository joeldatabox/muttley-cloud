package br.com.muttley.security.server.controller;

import br.com.muttley.domain.Service;
import br.com.muttley.exception.throwables.security.MuttleySecurityCredentialException;
import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.security.Authority;
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

import static br.com.muttley.security.server.property.MuttleySecurityProperty.TOKEN_HEADER_JWT;
import static java.util.Objects.isNull;

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

    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity save(
            @RequestBody final T value,
            final HttpServletResponse response,
            @RequestParam(required = false, value = "returnEntity", defaultValue = "") final String returnEntity,
            @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {

        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        this.checkRoleCreate(user);
        final T record = service.save(user, value);

        publishCreateResourceEvent(this.eventPublisher, response, record);

        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity update(@PathVariable("id") final String id, @RequestBody final T model, @RequestHeader(value = "${muttley.security.jwt.controller.token-header-jwt}", defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        checkRoleUpdate(user);
        model.setId(id);
        return ResponseEntity.ok(service.update(user, model));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity deleteById(@PathVariable("id") final String id, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        checkRoleDelete(user);
        service.deleteById(user, id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity findById(@PathVariable("id") final String id, final HttpServletResponse response, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        checkRoleRead(user);
        final T value = service.findById(user, id);
        publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(value);
    }

    @RequestMapping(value = "/first", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity first(final HttpServletResponse response, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        checkRoleRead(user);
        final T value = service.findFirst(user);
        publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(value);
    }

    @RequestMapping(value = "/{id}/historic", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity loadHistoric(@PathVariable("id") final String id, final HttpServletResponse response, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        checkRoleRead(user);
        final Historic historic = service.loadHistoric(user, id);
        publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(historic);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<PageableResource<T>> list(final HttpServletResponse response, @RequestParam final Map<String, String> allRequestParams,
                                                    @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        checkRoleRead(user);
        return ResponseEntity.ok(toPageableResource(eventPublisher, response, this.service, user, allRequestParams));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET, produces = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity count(@RequestParam final Map<String, String> allRequestParams, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        checkRoleRead(user);
        return ResponseEntity.ok(String.valueOf(service.count(user, allRequestParams)));
    }

    protected String[] getCreateRoles() {
        return null;
    }

    protected String[] getReadRoles() {
        return null;
    }

    protected String[] getUpdateRoles() {
        return null;
    }

    protected String[] getDeleteRoles() {
        return null;
    }

    protected final void checkRoleCreate(final User user) {
        if (!isNull(getCreateRoles()))
            this.checkCredentials(user, getCreateRoles());

    }

    protected final void checkRoleRead(final User user) {
        if (!isNull(getReadRoles()))
            this.checkCredentials(user, getReadRoles());
    }

    protected final void checkRoleUpdate(final User user) {
        if (!isNull(getUpdateRoles()))
            this.checkCredentials(user, this.getUpdateRoles());
    }

    protected final void checkRoleDelete(final User user) {
        if (!isNull(getDeleteRoles()))
            this.checkCredentials(user, this.getDeleteRoles());
    }

    protected final void checkCredentials(final User user, final String... roles) {
        if (!user.inAnyRole(roles)) {
            throw new MuttleySecurityCredentialException("Você não tem permissão para acessar este recurso ")
                    .addDetails("isNecessary", roles);
        }
    }

    protected final void checkCredentials(final User user, final Authority... roles) {
        if (!user.inAnyRole(roles)) {
            throw new MuttleySecurityCredentialException("Você não tem permissão para acessar este recurso ")
                    .addDetails("isNecessary", roles);
        }
    }
}
