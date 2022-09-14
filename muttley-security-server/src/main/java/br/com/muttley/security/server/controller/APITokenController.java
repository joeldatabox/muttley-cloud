package br.com.muttley.security.server.controller;

import br.com.muttley.model.security.APIToken;
import br.com.muttley.security.server.service.APITokenService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Joel Rodrigues Moreira on 09/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/api-tokens", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class APITokenController extends AbstractRestController<APIToken> {
    private final APITokenService service;

    @Autowired
    public APITokenController(APITokenService service, UserService userService, ApplicationEventPublisher eventPublisher) {
        super(service, userService, eventPublisher);
        this.service = service;
    }

    @RequestMapping(value = "/token/{token}", method = GET)
    public ResponseEntity getByToken(@PathVariable("token") final String token) {
        return ResponseEntity.ok(this.service.loadUserByAPIToken(token));
    }
}
