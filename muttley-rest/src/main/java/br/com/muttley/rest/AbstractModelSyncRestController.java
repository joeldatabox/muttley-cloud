package br.com.muttley.rest;

import br.com.muttley.domain.service.ModelSyncService;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.Document;
import br.com.muttley.model.ModelSync;
import br.com.muttley.model.jackson.DefaultDateFormatConfig;
import br.com.muttley.rest.hateoas.event.ModelSyncResourceCreatedEvent;
import br.com.muttley.security.infra.service.AuthService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;

/**
 * @author Joel Rodrigues Moreira on 06/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractModelSyncRestController<T extends ModelSync> extends AbstractRestController<T> implements RestResource<T>, ModelSyncRestController<T> {
    protected final ModelSyncService service;

    public AbstractModelSyncRestController(final ModelSyncService service, final AuthService userService, final ApplicationEventPublisher eventPublisher) {
        super(service, userService, eventPublisher);
        this.service = service;
    }

    @Override
    public void publishCreateResourceEvent(final ApplicationEventPublisher eventPublisher, final HttpServletResponse response, final Document model) {
        eventPublisher.publishEvent(new ModelSyncResourceCreatedEvent((ModelSync) model, response));
    }

    @RequestMapping(value = "/sync/{sync}", method = PUT, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity updateBySync(@PathVariable("sync") String sync, @RequestBody T model) {
        model.setSync(sync);
        return ResponseEntity.ok(this.service.updateBySync(this.userService.getCurrentUser(), model));
    }

    @RequestMapping(value = "/sync/{sync}", method = RequestMethod.DELETE, consumes = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE}, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity delteBySync(@PathVariable("sync") String sync) {
        service.deleteBySync(this.userService.getCurrentUser(), sync);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/sync/{sync}", method = GET, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity findBySync(@PathVariable("sync") final String sync, final HttpServletResponse response) {
        final T value = (T) this.service.findBySync(this.userService.getCurrentUser(), sync);
        this.publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(value);
    }

    @Override
    @RequestMapping(value = "/reference/sync/{sync}", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity findReferenceBySync(@PathVariable("sync") final String sync, final HttpServletResponse response) {
        final T value = (T) this.service.findReferenceBySync(this.userService.getCurrentUser(), sync);
        this.publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(value);
    }

    @RequestMapping(value = "/syncOrId/{syncOrId}", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE})
    @ResponseStatus(OK)
    @Override
    public ResponseEntity findBySyncOrId(@PathVariable("syncOrId") final String syncOrId, final HttpServletResponse response) {
        final T value = (T) this.service.findByIdOrSync(this.userService.getCurrentUser(), syncOrId);
        this.publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(value);
    }

    @RequestMapping(value = "/syncs", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    @Override
    public ResponseEntity findBySyncs(@RequestParam(required = false, value = "syncs") final String[] syncs, final HttpServletResponse response) {
        final Set<T> values = service.getIdsOfSyncs(this.userService.getCurrentUser(), new HashSet<>(asList(syncs)));

        publishSingleResourceRetrievedEvent(this.eventPublisher, response);

        return ResponseEntity.ok(values);
    }

    @RequestMapping(value = "/synchronization", method = PUT, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity synchronization(@RequestBody final List<T> values) {
        service.synchronize(this.userService.getCurrentUser(), values);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/lastModify", method = GET, produces = TEXT_PLAIN_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity getLastModify(@Autowired final DefaultDateFormatConfig dateFormat) {
        final Date result = this.service.getLastModify(this.userService.getCurrentUser());
        return ResponseEntity.ok(result == null ? null : dateFormat.format(result));
    }

    @RequestMapping(value = "/sync/{sync}/id", method = GET, produces = TEXT_PLAIN_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity getIdOfSync(@PathVariable("sync") final String sync) {
        final String objectId = this.service.getIdOfSync(this.userService.getCurrentUser(), sync);
        return ResponseEntity.ok(objectId);
    }

    protected String getPathVariable(final String key) {
        final Map<String, String> pathVariavles = (Map<String, String>) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()).getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        return pathVariavles.get(key);
    }

    protected String isValidOrNotFoud(final String id, final Class clazz) {
        if (!ObjectId.isValid(id)) {
            throw new MuttleyNotFoundException(clazz, "id", "Registro não encontrado");
        }
        return id;
    }
}
