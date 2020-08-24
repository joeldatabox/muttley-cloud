package br.com.muttley.security.server.controller;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.security.server.service.UserService;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Joel Rodrigues Moreira on 23/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/work-teams", produces = APPLICATION_JSON_VALUE)
public class WorkTeamController extends AbstractRestController<WorkTeam> {
    private WorkTeamService service;

    @Autowired
    public WorkTeamController(final WorkTeamService service, final UserService userService, final ApplicationEventPublisher eventPublisher) {
        super(service, userService, eventPublisher);
        this.service = service;
    }

    @RequestMapping(value = "/roles/current-roles", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity loadCurrentRoles(final HttpServletResponse response, @RequestHeader(value = "${muttley.security.jwt.controller.token-header-jwt}", defaultValue = "") final String tokenHeader) {
        return ResponseEntity.ok(service.loadCurrentRoles(userService.getUserFromToken(new JwtToken(tokenHeader))));
    }

    @RequestMapping(value = "/avaliable-roles", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity loadAvaliableRoles(final HttpServletResponse response, @RequestHeader(value = "${muttley.security.jwt.controller.token-header-jwt}", defaultValue = "") final String tokenHeader) {
        return ResponseEntity.ok(service.loadAvaliableRoles(userService.getUserFromToken(new JwtToken(tokenHeader))));
    }

    @RequestMapping(value = "/find-by-name", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity findByName(@RequestParam(name = "name", defaultValue = "") final String name, @RequestHeader(value = "${muttley.security.jwt.controller.token-header-jwt}", defaultValue = "") final String tokenHeader) {
        return ResponseEntity.ok(this.service.findByName(userService.getUserFromToken(new JwtToken(tokenHeader)), name));
    }

    @RequestMapping(value = "/find-by-user", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity findByUser(@RequestHeader(value = "${muttley.security.jwt.controller.token-header-jwt}", defaultValue = "") final String tokenHeader) {
        return ResponseEntity.ok(service.findByUser(userService.getUserFromToken(new JwtToken(tokenHeader))));
    }
}
