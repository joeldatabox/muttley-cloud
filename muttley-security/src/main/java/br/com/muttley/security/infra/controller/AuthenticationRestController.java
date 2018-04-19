package br.com.muttley.security.infra.controller;

import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUserNameOrPasswordInvalidException;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.UserPayLoadLogin;
import br.com.muttley.model.security.events.UserLoggedEvent;
import br.com.muttley.security.feign.auth.AuthenticationRestServiceClient;
import br.com.muttley.security.feign.auth.AuthenticationTokenServiceClient;
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
    protected final AuthenticationRestServiceClient authenticationRestService;
    protected final AuthenticationTokenServiceClient authenticationTokenService;

    @Autowired
    public AuthenticationRestController(
            final @Value("${muttley.security.jwt.controller.tokenHeader:Authorization}") String tokenHeader,
            final AuthenticationManager authenticationManager,
            final AuthenticationRestServiceClient authenticationRestService,
            final AuthenticationTokenServiceClient authenticationTokenService,
            final ApplicationEventPublisher eventPublisher) {
        this.tokenHeader = tokenHeader;
        this.authenticationManager = authenticationManager;
        this.authenticationRestService = authenticationRestService;
        this.authenticationTokenService = authenticationTokenService;
        this.eventPublisher = eventPublisher;
    }

    @RequestMapping(value = "${muttley.security.jwt.controller.loginEndPoint}", method = RequestMethod.POST)
    public ResponseEntity createAuthenticationToken(@RequestBody final Map<String, String> payload, Device device, HttpServletRequest request) {
        checkPayloadContainsUserNameAndPasswdOndy(payload);

        checkPayloadSize(payload);

        try {

            //pegando o token do serviço de usuários
            final JwtToken jwtToken = this.authenticationRestService.createAuthenticationToken(new UserPayLoadLogin(payload.get(USERNAME), payload.get(PASSWORD)));
            //pegando usuário do token
            final JwtUser userDetails = this.authenticationTokenService.getUserFromToken(jwtToken);


            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities())
            );

            //despachando a requisição para a validação do spring
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //se chegou até aqui é sinal que o usuário e senha é valido
            //notificando que o token foi gerado
            this.afterGeneratedToken(userDetails, jwtToken);
            //lançando evento de usuário logado
            this.eventPublisher.publishEvent(new UserLoggedEvent(userDetails.getOriginUser()));
            //devolvendo token gerado
            return ResponseEntity.ok(jwtToken);
        } catch (BadCredentialsException ex) {
            throw new MuttleySecurityUserNameOrPasswordInvalidException();
        }
    }

    @RequestMapping(value = "${muttley.security.jwt.controller.refreshEndPoint}", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        JwtToken token = new JwtToken(request.getHeader(tokenHeader));
        return ResponseEntity.ok(this.authenticationRestService.refreshAndGetAuthenticationToken(token));
    }

    /**
     * O Metodo é notificado toda vez que um token é gerado para um determinado usuário
     *
     * @param user  -> usuário do token gerado
     * @param token -> token gerado
     */
    protected void afterGeneratedToken(final JwtUser user, final JwtToken token) {
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
