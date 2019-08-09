package br.com.muttley.rest;

import br.com.muttley.domain.service.Service;
import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.rest.hateoas.resource.PageableResource;
import br.com.muttley.security.feign.UserPreferenceServiceClient;
import br.com.muttley.security.infra.service.AuthService;
import org.springframework.context.ApplicationEventPublisher;
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

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public abstract class AbstractRestController<T extends Document> implements RestResource<T>, RestController<T> {
    protected final Service<T> service;
    protected final AuthService userService;
    protected final ApplicationEventPublisher eventPublisher;
    protected final UserPreferenceServiceClient userPreferenceService;

    public AbstractRestController(final Service service, final AuthService userService, final ApplicationEventPublisher eventPublisher, final UserPreferenceServiceClient userPreferenceService) {
        this.service = service;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.userPreferenceService = userPreferenceService;
    }

    @Override
    @RequestMapping(method = POST, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity save(@RequestBody final T value, final HttpServletResponse response, @RequestParam(required = false, value = "returnEntity", defaultValue = "") final String returnEntity) {
        final T record = service.save(this.userService.getCurrentUser(), value);

        publishCreateResourceEvent(this.eventPublisher, response, record);

        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity update(@PathVariable("id") final String id, @RequestBody final T model) {
        model.setId(id);
        return ResponseEntity.ok(service.update(this.userService.getCurrentUser(), model));
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity deleteById(@PathVariable("id") final String id) {
        service.deleteById(this.userService.getCurrentUser(), id);
        return ResponseEntity.ok().build();
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity findById(@PathVariable("id") final String id, final HttpServletResponse response) {
        final T value = service.findById(this.userService.getCurrentUser(), id);

        publishSingleResourceRetrievedEvent(this.eventPublisher, response);

        return ResponseEntity.ok(value);
    }

    @Override
    @RequestMapping(value = "/first", method = RequestMethod.GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity first(final HttpServletResponse response) {
        final T value = service.findFirst(this.userService.getCurrentUser());

        publishSingleResourceRetrievedEvent(this.eventPublisher, response);

        return ResponseEntity.ok(value);
    }

    @Override
    @RequestMapping(value = "/{id}/historic", method = RequestMethod.GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity loadHistoric(@PathVariable("id") final String id, final HttpServletResponse response) {
        final Historic historic = service.loadHistoric(this.userService.getCurrentUser(), id);

        publishSingleResourceRetrievedEvent(this.eventPublisher, response);

        return ResponseEntity.ok(historic);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<PageableResource> list(final HttpServletResponse response, @RequestParam final Map<String, String> allRequestParams) {
        return ResponseEntity.ok(toPageableResource(eventPublisher, response, this.service, this.userService.getCurrentUser(), allRequestParams));
    }

    @Override
    @RequestMapping(value = "/count", method = RequestMethod.GET, produces = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity count(final Map<String, Object> allRequestParams) {
        return ResponseEntity.ok(String.valueOf(service.count(this.userService.getCurrentUser(), allRequestParams)));
    }
}
