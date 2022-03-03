package br.com.muttley.security.server.controller;

import br.com.muttley.model.WorkTeam;
import br.com.muttley.security.server.service.UserService;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Joel Rodrigues Moreira on 23/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/work-teams", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class WorkTeamController extends AbstractRestController<WorkTeam> {
    private final WorkTeamService service;

    @Autowired
    public WorkTeamController(final WorkTeamService service, final UserService userService, final ApplicationEventPublisher eventPublisher) {
        super(service, userService, eventPublisher);
        this.service = service;
    }
}
