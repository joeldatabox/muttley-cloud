package br.com.muttley.hermes.server.controller;

import br.com.muttley.hermes.server.service.UserTokensNotificationService;
import br.com.muttley.model.hermes.notification.TokenId;
import br.com.muttley.rest.RestResource;
import br.com.muttley.security.infra.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
@RequestMapping(value = "/api/v1/tokens-notification", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class UserTokenNotificationController implements RestResource {
    private final AuthService authService;
    private final UserTokensNotificationService service;


    @Autowired
    public UserTokenNotificationController(final AuthService authService, final UserTokensNotificationService service) {
        this.authService = authService;
        this.service = service;
    }

    @RequestMapping(method = POST, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody TokenId tokenId) {
        this.service.addTokenNotification(this.authService.getCurrentUser(), tokenId);
    }

}
