package br.com.muttley.security.server.controller.auth;

import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUserNameOrPasswordInvalidException;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.events.UserLoggedEvent;
import br.com.muttley.security.server.service.JwtTokenUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * RestController responsavel por criar tokens para serviços client-s
 *
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/users/authentication", produces = APPLICATION_JSON_VALUE)
public class AuthenticationRestController {

    protected static final String USERNAME = "username";
    protected static final String PASSWORD = "password";

    protected final ApplicationEventPublisher eventPublisher;
    protected final AuthenticationManager authenticationManager;
    protected final JwtTokenUtilService jwtTokenUtil;
    protected final UserDetailsService userDetailsService;

    @Autowired
    public AuthenticationRestController(final AuthenticationManager authenticationManager, final JwtTokenUtilService jwtTokenUtil, final UserDetailsService userDetailsService, final ApplicationEventPublisher eventPublisher) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.eventPublisher = eventPublisher;
    }

    @RequestMapping(value = "/login", method = POST)
    public ResponseEntity createAuthenticationToken(@RequestBody final Map<String, String> payload, Device device, HttpServletRequest request) {
        checkPayloadContainsUserNameAndPasswdOndy(payload);
        checkPayloadSize(payload);

        try {

            //se chegou até aqui é sinal que o usuário e senha é valido
            final JwtUser userDetails = (JwtUser) userDetailsService.loadUserByUsername(payload.get(USERNAME));
            //gerando o token de autorização
            JwtToken token = new JwtToken(jwtTokenUtil.generateToken(userDetails, device));
            //lançando evento de usuário logado
            this.eventPublisher.publishEvent(new UserLoggedEvent(token, userDetails.getOriginUser()));
            //devolvendo token gerado
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException | MuttleySecurityUnauthorizedException ex) {
            throw new MuttleySecurityUserNameOrPasswordInvalidException();
        }
    }

    @RequestMapping(value = "/refresh", method = POST)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(@RequestBody JwtToken token) {
        String username = jwtTokenUtil.getUsernameFromToken(token.getToken());
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        if (jwtTokenUtil.canTokenBeRefreshed(token.getToken(), user.getLastPasswordResetDate())) {
            String refreshedToken = jwtTokenUtil.refreshToken(token.getToken());
            return ResponseEntity.ok(new JwtToken(refreshedToken));
        }
        throw new MuttleySecurityBadRequestException(null, null, "Token invalido. Faça login novamente");
    }

    private final void checkPayloadContainsUserNameAndPasswdOndy(final Map<String, String> payload) {
        if (payload.isEmpty() || payload.size() < 2 || !payload.containsKey(USERNAME) || !payload.containsKey(PASSWORD)) {
            throw new MuttleySecurityBadRequestException(User.class, null, "Informe os campos de usuário e senha")
                    .addDetails(USERNAME, "algum usuário válido")
                    .addDetails(PASSWORD, "uma senha válida!");
        }
    }

    private final void checkPayloadSize(final Map<String, String> payload) {
        if (payload.size() > 2) {
            throw new MuttleySecurityBadRequestException(User.class, null, "Por favor informe somente os campos de usuário e senha")
                    .addDetails(USERNAME, "algum usuário válido")
                    .addDetails(PASSWORD, "uma senha válida!");
        }
    }
}
