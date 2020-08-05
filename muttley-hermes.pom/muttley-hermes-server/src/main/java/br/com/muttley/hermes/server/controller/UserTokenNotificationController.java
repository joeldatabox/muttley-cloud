package br.com.muttley.hermes.server.controller;

import br.com.muttley.headers.components.MuttleyUserAgent;
import br.com.muttley.hermes.server.service.UserTokensNotificationService;
import br.com.muttley.model.hermes.notification.TokenId;
import br.com.muttley.rest.RestResource;
import br.com.muttley.security.infra.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joel Rodrigues Moreira on 03/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/tokens-notification/", produces = APPLICATION_JSON_VALUE)
public class UserTokenNotificationController implements RestResource {
    private final AuthService authService;
    private final UserTokensNotificationService service;
    @Autowired
    private MuttleyUserAgent userAgent;


    @Autowired
    public UserTokenNotificationController(final AuthService authService, final UserTokensNotificationService service) {
        this.authService = authService;
        this.service = service;
    }

    @RequestMapping(
            method = POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    @ResponseStatus(CREATED)
    public void save(@RequestBody TokenId tokenId, HttpServletResponse response) {
        tokenId.setMobile(this.userAgent.isMobile());
        this.service.addTokenNotification(this.authService.getCurrentUser(), tokenId);
        /*T record = this.service.save(this.userService.getCurrentUser(), value);
        this.publishCreateResourceEvent(this.eventPublisher, response, record);
        return returnEntity != null && returnEntity.equals("true") ? ResponseEntity.status(HttpStatus.CREATED).body(record) : ResponseEntity.status(HttpStatus.CREATED).build();*/
    }
}
