package br.com.muttley.security.gateway.controller;

import br.com.muttley.model.Historic;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.security.infra.feign.WorkTeamServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;


/**
 * @author Joel Rodrigues Moreira on 26/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/secured/api/v1/work-teams", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class WorkTeamController {
    private final WorkTeamServiceClient client;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public WorkTeamController(final WorkTeamServiceClient client, final ApplicationEventPublisher eventPublisher) {
        this.client = client;
        this.eventPublisher = eventPublisher;
    }

    @RequestMapping(method = POST, consumes = {APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity save(@RequestBody final WorkTeam value, final HttpServletResponse response, @RequestParam(required = false, value = "returnEntity", defaultValue = "") final String returnEntity) {
        final WorkTeam record = client.merger(value);

        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @RequestMapping(value = "/{id}", method = PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity update(@PathVariable("id") final String id, @RequestBody final WorkTeam model) {
        return ResponseEntity.ok(client.update(id, model));
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public ResponseEntity deleteById(@PathVariable("id") final String id) {
        client.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findById(@PathVariable("id") final String id, final HttpServletResponse response) {
        final WorkTeam value = client.findById(id);
        return ResponseEntity.ok(value);
    }


    @RequestMapping(value = "/first", method = GET, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity first(final HttpServletResponse response) {
        final WorkTeam value = client.first();
        return ResponseEntity.ok(value);
    }

    @RequestMapping(value = "/{id}/historic", method = GET, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity loadHistoric(@PathVariable("id") final String id, final HttpServletResponse response) {
        final Historic historic = client.loadHistoric(id);
        return ResponseEntity.ok(historic);
    }


    @RequestMapping(value = "/count", method = GET, produces = TEXT_PLAIN_VALUE)
    public ResponseEntity count(@RequestParam final Map<String, String> allRequestParams) {
        return ResponseEntity.ok(client.count(allRequestParams));
    }

    @RequestMapping(value = "/roles/current-roles", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity loadCurrentRoles() {
        return ResponseEntity.ok(client.loadCurrentRoles());
    }

    @RequestMapping(value = "/avaliable-roles", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity loadAvaliableRoles(final HttpServletResponse response, @RequestHeader(value = "${muttley.security.jwt.controller.token-header-jwt}", defaultValue = "") final String tokenHeader) {
        return ResponseEntity.ok(client.loadAvaliableRoles());
    }

    /*@RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<PageableResource> list(@RequestParam final Map<String, String> allRequestParams) {
        final PageableResource pageableResource = client.list(allRequestParams);
        return ResponseEntity.ok(pageableResource);
    }*/
}
