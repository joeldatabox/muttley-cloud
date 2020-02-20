package br.com.muttley.security.server.controller.auth;

import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.security.server.repository.UserPreferencesRepository;
import br.com.muttley.security.server.service.JwtTokenUtilService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Aplica o filtro de autenticação necessario
 */
@RestController
@RequestMapping(value = "/api/v1/users/authentication", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class AuthenticationTokenController {
    private final JwtTokenUtilService tokenUtil;
    private final UserService userService;
    private final UserPreferencesRepository preferencesRepository;

    public AuthenticationTokenController(final JwtTokenUtilService tokenUtil, final UserService userService, final UserPreferencesRepository preferencesRepository) {
        this.tokenUtil = tokenUtil;
        this.userService = userService;
        this.preferencesRepository = preferencesRepository;
    }

    @RequestMapping(value = "/user-from-token", method = RequestMethod.POST)
    public ResponseEntity getUserFromToken(final @RequestBody JwtToken token) {
        if (!token.isEmpty()) {
            final String userName = this.tokenUtil.getUsernameFromToken(token.getToken());
            if (!isNullOrEmpty(userName)) {
                //buscando o usuário  presente no token
                final JwtUser jwtUser = (JwtUser) this.userService.loadUserByUsername(userName);
                //buscando as preferencias de usuário
                jwtUser.getOriginUser().setPreferences(this.preferencesRepository.findByUser(jwtUser.getOriginUser()));

                //verificando a validade do token
                if (tokenUtil.validateToken(token.getToken(), jwtUser)) {
                    return ResponseEntity.ok(jwtUser.toJson());
                }
            }
        }
        throw new MuttleySecurityUnauthorizedException();
    }
}
