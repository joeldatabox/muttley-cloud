package br.com.muttley.security.infra.controller;

import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUserNameOrPasswordInvalidException;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.security.infra.component.util.JwtTokenUtil;
import br.com.muttley.security.infra.events.UserLoggedEvent;
import br.com.muttley.security.infra.response.JwtTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * RestController responsavel por despachar novas requisições de token
 *
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
public class AuthenticationRestController {

    protected final String tokenHeader;
    protected static final String USERNAME = "username";
    protected static final String PASSWORD = "password";

    protected final ApplicationEventPublisher eventPublisher;
    protected final AuthenticationManager authenticationManager;
    protected final JwtTokenUtil jwtTokenUtil;
    protected final UserDetailsService userDetailsService;

    @Autowired
    public AuthenticationRestController(final @Value("${muttley.security.jwt.controller.tokenHeader:Authorization}") String tokenHeader, final AuthenticationManager authenticationManager, final JwtTokenUtil jwtTokenUtil, final UserDetailsService userDetailsService, final ApplicationEventPublisher eventPublisher) {
        this.tokenHeader = tokenHeader;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.eventPublisher = eventPublisher;
    }

    @RequestMapping(value = "${muttley.security.jwt.controller.loginEndPoint}", method = RequestMethod.POST)
    public ResponseEntity createAuthenticationToken(@RequestBody final Map<String, String> payload, Device device, HttpServletRequest request) {
        checkPayloadContainsUserNameAndPasswdOndy(payload);

        checkPayloadSize(payload);

        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            payload.get(USERNAME),
                            payload.get(PASSWORD)
                    )
            );

            //despachando a requisição para a validação do spring
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //se chegou até aqui é sinal que o usuário e senha é valido
            final JwtUser userDetails = (JwtUser) userDetailsService.loadUserByUsername(payload.get(USERNAME));
            //gerando o token de autorização
            JwtTokenResponse token = new JwtTokenResponse(jwtTokenUtil.generateToken(userDetails, device));
            //notificando que o token foi gerado
            this.afterGeneratedToken(userDetails, token);
            //lançando evento de usuário logado
            this.eventPublisher.publishEvent(new UserLoggedEvent(userDetails.getOriginUser()));
            //devolvendo token gerado
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException ex) {
            throw new MuttleySecurityUserNameOrPasswordInvalidException();
        }
    }

    @RequestMapping(value = "${muttley.security.jwt.controller.refreshEndPoint}", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);

        if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
            String refreshedToken = jwtTokenUtil.refreshToken(token);
            return ResponseEntity.ok(new JwtTokenResponse(refreshedToken));
        }
        throw new MuttleySecurityBadRequestException(null, null, "Token invalido. Faça login novamente");
    }

    /**
     * O Metodo é notificado toda vez que um token é gerado para um determinado usuário
     *
     * @param user  -> usuário do token gerado
     * @param token -> token gerado
     */
    protected void afterGeneratedToken(final JwtUser user, final JwtTokenResponse token) {
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
