package br.com.muttley.security.server.controller;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.XAPIToken;
import br.com.muttley.security.server.service.UserService;
import br.com.muttley.security.server.service.XAPITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joel Rodrigues Moreira on 09/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/x-api-tokens", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class XAPITokenController extends AbstractRestController<XAPIToken> {
    private final XAPITokenService service;

    @Autowired
    public XAPITokenController(XAPITokenService service, UserService userService, ApplicationEventPublisher eventPublisher) {
        super(service, userService, eventPublisher);
        this.service = service;
    }

    @RequestMapping(value = "/generate-x-api-token", method = POST)
    public ResponseEntity generateXAPIToken(@RequestHeader(value = "${muttley.security.jwt.controller.tokenHeader-jwt}", defaultValue = "") final String tokenHeader) {
        return ResponseEntity.ok(this.service.generateXAPIToken(this.userService.getUserFromToken(new JwtToken(tokenHeader))));
    }

    @RequestMapping(value = "/token", method = GET)
    public ResponseEntity getByToken(@RequestParam("token") final String token) {
        return ResponseEntity.ok(this.service.loadUserByAPIToken(token));
    }

}
