package br.com.muttley.admin.server.controller;

import br.com.muttley.admin.server.events.OwnerCreatedEvent;
import br.com.muttley.model.Historic;
import br.com.muttley.model.security.Owner;
import br.com.muttley.rest.RestController;
import br.com.muttley.rest.RestResource;
import br.com.muttley.security.feign.OwnerServiceClient;
import br.com.muttley.security.feign.WorkTeamServiceClient;
import br.com.muttley.security.infra.resource.PageableResource;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
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
@org.springframework.web.bind.annotation.RestController
@RequestMapping(value = "/api/v1/owners", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class OwnerController implements RestController<Owner>, RestResource {
    private final OwnerServiceClient client;
    private final WorkTeamServiceClient workTeamService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public OwnerController(final OwnerServiceClient client, final WorkTeamServiceClient workTeamService, final ApplicationEventPublisher eventPublisher) {
        this.client = client;
        this.workTeamService = workTeamService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @RequestMapping(method = POST, consumes = {APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity save(@RequestBody final Owner value, final HttpServletResponse response, @RequestParam(required = false, value = "returnEntity", defaultValue = "") final String returnEntity) {

        final Owner record = client.save(value, "true");

        //disparando evento para informar que foi cria o owner
        this.eventPublisher.publishEvent(new OwnerCreatedEvent(record));
        /*//criando o grupo de trabalho para vendedores
        final WorkTeam vendedores = this.workTeamService.createWorkTeamFor(
                record.getId(), new WorkTeam()
                        .setName(WORK_TEAM_NAME)
                        .setDescription("Grupo principal do sistema criado especificamente para dar autorização a vendedores mobile")
                        .setUserMaster(record.getUserMaster())
                        .setUserMaster(record.getUserMaster())
                        .setOwner(record)
                        .setRoles(
                                Role.getValues()
                                        .stream()
                                        .filter(it -> it.getRoleName().contains("MOBILE"))
                                        .collect(toSet())
                        )
        );*/


        publishCreateResourceEvent(this.eventPublisher, response, record);

        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @Override
    @RequestMapping(value = "/{id}", method = PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity update(@PathVariable("id") final String id, @RequestBody final Owner model) {
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
        final Owner value = client.findById(id);

        publishSingleResourceRetrievedEvent(this.eventPublisher, response);

        return ResponseEntity.ok(value);
    }

    @Override
    @RequestMapping(value = "/reference/{id}", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity findReferenceById(@PathVariable("id") String id, HttpServletResponse response) {
        final Owner value = client.findReferenceById(id);

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
        final Owner value = client.first();
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
