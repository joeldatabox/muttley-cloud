package br.com.muttley.security.server.controller;


import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserView;
import br.com.muttley.mongo.service.infra.Operator;
import br.com.muttley.rest.hateoas.resource.MetadataPageable;
import br.com.muttley.rest.hateoas.resource.PageableResource;
import br.com.muttley.security.server.service.UserService;
import br.com.muttley.security.server.service.UserViewService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Joel Rodrigues Moreira on 29/04/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/users-view", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class UserViewController extends AbstractRestController<UserView> {
    final UserViewService service;

    public UserViewController(final UserViewService service, final UserService userService, final ApplicationEventPublisher eventPublisher) {
        super(service, userService, eventPublisher);
        this.service = service;
    }

    @Override
    @RequestMapping(method = GET)
    public ResponseEntity<PageableResource> list(final HttpServletResponse response, @RequestParam final Map<String, String> allRequestParams,
                                                 @RequestHeader(value = "${muttley.security.jwt.controller.tokenHeader-jwt}", defaultValue = "") final String tokenHeader) {
        //validando os parametros passados
        final Map<String, String> params = validPageable(allRequestParams);
        final Long SKIP = allRequestParams.containsKey(Operator.SKIP.toString()) ? Long.valueOf(allRequestParams.get(Operator.SKIP.toString()).toString()) : 0l;
        final Long LIMIT = allRequestParams.containsKey(Operator.LIMIT.toString()) ? Long.valueOf(allRequestParams.get(Operator.LIMIT.toString()).toString()) : 100l;

        final long total = service.count(allRequestParams.get("q"), allRequestParams.get("owner"));


        if (total == 0) {
            throw new MuttleyNoContentException(null, null, "registros não encontrados!");
        }
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        final Owner owner = user.getCurrentOwner();
        final long totalAll = service.count(null, owner != null ? owner.getId() : null);
        final List records = service.list(allRequestParams.get("q"), owner != null ? owner.getId() : null);

        final Long recordSize = Long.valueOf(records.size());

        final MetadataPageable metadataPageable = new MetadataPageable(
                ServletUriComponentsBuilder.fromCurrentRequest(),
                LIMIT,
                SKIP,
                recordSize,
                total,
                totalAll);

        publishPaginatedResultsRetrievedEvent(
                eventPublisher,
                response,
                ServletUriComponentsBuilder.fromCurrentRequest(),
                metadataPageable
        );


        return ResponseEntity.ok(new PageableResource(records, metadataPageable));




        /*final String owner = allRequestParams.get("owner");
        System.out.println(this.service.list(allRequestParams.get("q"), owner));
        return ResponseEntity.ok().build();*/
    }

    @RequestMapping(value = "/userName", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity findById(@RequestParam(value = "userName", required = false, defaultValue = "null") final String userName, final HttpServletResponse response, @RequestParam final Map<String, String> allRequestParams,
                                   @RequestHeader(value = "${muttley.security.jwt.controller.tokenHeader-jwt}", defaultValue = "") final String tokenHeader) {

        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        final Owner owner = user.getCurrentOwner();

        final UserView value = service.findByUserName(userName, owner != null ? owner.getId() : null);

        publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(value);
    }

    @RequestMapping(value = "/count", method = GET, produces = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity count(@RequestParam final Map<String, String> allRequestParams, @RequestHeader(value = "${muttley.security.jwt.controller.tokenHeader-jwt}", defaultValue = "") final String tokenHeader) {
        final User user = this.userService.getUserFromToken(new JwtToken(tokenHeader));
        final Owner owner = user.getCurrentOwner();
        checkRoleRead(user);
        return ResponseEntity.ok(String.valueOf(service.count(allRequestParams.get("q"), owner != null ? owner.getId() : null)));
    }
}
