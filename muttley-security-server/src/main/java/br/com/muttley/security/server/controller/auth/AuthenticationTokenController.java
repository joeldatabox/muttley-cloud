package br.com.muttley.security.server.controller.auth;

import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.localcache.services.LocalDatabindingService;
import br.com.muttley.localcache.services.LocalUserPreferenceService;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.Password;
import br.com.muttley.model.security.User;
import br.com.muttley.security.server.events.CurrentOwnerResolverEvent;
import br.com.muttley.security.server.service.JwtTokenUtilService;
import br.com.muttley.security.server.service.PasswordService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Aplica o filtro de autenticação necessario
 */
@RestController
@RequestMapping(value = "/api/v1/users/authentication", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class AuthenticationTokenController {
    private final JwtTokenUtilService tokenUtil;
    private final UserService userService;
    private final LocalUserPreferenceService preferencesService;
    private final LocalDatabindingService dataBindingService;
    private final PasswordService passwordService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public AuthenticationTokenController(
            final JwtTokenUtilService tokenUtil,
            final UserService userService,
            final LocalUserPreferenceService preferencesService,
            final LocalDatabindingService dataBindingService,
            final PasswordService passwordService,
            final ApplicationEventPublisher eventPublisher) {
        this.tokenUtil = tokenUtil;
        this.userService = userService;
        this.preferencesService = preferencesService;
        this.dataBindingService = dataBindingService;
        this.passwordService = passwordService;
        this.eventPublisher = eventPublisher;
    }

    @RequestMapping(value = "/user-from-token", method = RequestMethod.POST)
    public ResponseEntity getUserFromToken(final @RequestBody JwtToken token) {
        if (!token.isEmpty()) {
            final String userName = this.tokenUtil.getUsernameFromToken(token.getToken());
            if (!isNullOrEmpty(userName)) {
                //buscando o usuário  presente no token
                final User user = this.userService.findByUserName(userName);
                //buscando as preferencias de usuário

                user.setPreferences(this.preferencesService.getUserPreferences(token, user));
                //disparando evento para resolver o owner corrent
                final CurrentOwnerResolverEvent event = new CurrentOwnerResolverEvent(user);
                this.eventPublisher.publishEvent(event);
                //buscando os databindinqs do usuário
                user.setDataBindings(this.dataBindingService.getUserDataBindings(token, user));
                final Password password = this.passwordService.findByUserId(user.getId());

                //verificando a validade do token
                if (tokenUtil.validateTokenWithUser(token.getToken(), user, password)) {
                    return ResponseEntity.ok(JwtUser.Builder.newInstance().set(user).setPassword(password).build().toJson());
                }
            }
        }
        throw new MuttleySecurityUnauthorizedException();
    }

}
