package br.com.muttley.admin.server.controller;

import br.com.muttley.model.Historic;
import br.com.muttley.model.security.AccessPlan;
import br.com.muttley.rest.RestController;
import br.com.muttley.rest.RestResource;
import br.com.muttley.security.feign.AccessPlanServiceClient;
import br.com.muttley.security.infra.resource.PageableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Joel Rodrigues Moreira 20/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class AccessPlanController implements RestController<AccessPlan>, RestResource {
    private final AccessPlanServiceClient client;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public AccessPlanController(final AccessPlanServiceClient client, final ApplicationEventPublisher eventPublisher) {
        this.client = client;
        this.eventPublisher = eventPublisher;
    }
    @Override
    @RequestMapping(method = POST, consumes = {APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity save(@RequestBody final AccessPlan value, final HttpServletResponse response, @RequestParam(required = false, value = "returnEntity", defaultValue = "") final String returnEntity) {
        final AccessPlan record = client.save(value, returnEntity);

        publishCreateResourceEvent(this.eventPublisher, response, record);

        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @Override
    @RequestMapping(value = "/{id}", method = PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity update(@PathVariable("id") final String id, @RequestBody final AccessPlan model) {
        return ResponseEntity.ok(client.update(id, model));
    }

    @Override
    @RequestMapping(value = "/{id}", method = DELETE)
    public ResponseEntity deleteById(@PathVariable("id") final String id) {
        client.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @RequestMapping(value = "/{id}", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findById(@PathVariable("id") final String id, final HttpServletResponse response) {
        final AccessPlan value = client.findById(id);

        publishSingleResourceRetrievedEvent(this.eventPublisher, response);

        return ResponseEntity.ok(value);
    }

    @Override
    public ResponseEntity findByIds(final String[] strings, final HttpServletResponse httpServletResponse) {
        throw new NotImplementedException();
    }

    @Override
    @RequestMapping(value = "/first", method = GET, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity first(final HttpServletResponse response) {
        final AccessPlan value = client.first();
        publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(value);
    }

    @Override
    @RequestMapping(value = "/{id}/historic", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity loadHistoric(@PathVariable("id") final String id, final HttpServletResponse response) {
        final Historic historic = client.loadHistoric(id);
        publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(historic);
    }

    @Override
    @RequestMapping(method = GET, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity list(final HttpServletResponse response, @RequestParam final Map<String, String> allRequestParams) {
        final PageableResource pageableResource = client.list(allRequestParams);
        return ResponseEntity.ok(pageableResource);
    }

    @Override
    @RequestMapping(value = "/count", method = GET, produces = TEXT_PLAIN_VALUE)
    public ResponseEntity count(@RequestParam final Map<String, String> allRequestParams) {
        return ResponseEntity.ok(String.valueOf(client.count(allRequestParams)));
    }

}
