package br.com.muttley.security.gateway.controller;

import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUserNameOrPasswordInvalidException;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.UserPayLoadLogin;
import br.com.muttley.model.security.events.UserLoggedEvent;
import br.com.muttley.security.gateway.properties.MuttleySecurityProperties;
import br.com.muttley.security.infra.feign.auth.AuthenticationRestServiceClient;
import br.com.muttley.security.infra.services.CacheUserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static br.com.muttley.security.gateway.properties.MuttleySecurityProperties.LOGIN_END_POINT;
import static br.com.muttley.security.gateway.properties.MuttleySecurityProperties.REFRESH_END_POINT;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * RestController responsavel por despachar novas requisições de token
 *
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@RestController
public class AuthenticationRestController {

    @Autowired
    private MuttleySecurityProperties property;

    protected static final String USERNAME = "username";
    protected static final String PASSWORD = "password";

    protected final ApplicationEventPublisher eventPublisher;
    protected final AuthenticationManager authenticationManager;
    protected final AuthenticationRestServiceClient authenticationRestService;
    protected final CacheUserAuthenticationService cacheAuthService;

    @Autowired
    public AuthenticationRestController(
            final AuthenticationManager authenticationManager,
            final AuthenticationRestServiceClient authenticationRestService,
            final ApplicationEventPublisher eventPublisher,
            final CacheUserAuthenticationService cacheAuthService) {
        this.authenticationManager = authenticationManager;
        this.authenticationRestService = authenticationRestService;
        this.eventPublisher = eventPublisher;
        this.cacheAuthService = cacheAuthService;

    }

    @RequestMapping(value = LOGIN_END_POINT, method = RequestMethod.POST)
    public ResponseEntity createAuthenticationToken(@RequestBody final UserPayLoadLogin payload) {
        checkPayloadContainsUserNameAndPasswdOndy(payload);

        //checkPayloadSize(payload);

        try {

            //pegando o token do serviço de usuários
            final JwtToken jwtToken = this.authenticationRestService.createAuthenticationToken(payload);
            //pegando usuário do token
            //final JwtUser userDetails = this.authenticationTokenService.getUserFromToken(jwtToken);

            //gerando a authenticação do serviço
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(payload.getUsername(), payload.getPassword())
            );

            final JwtUser userDetails = (JwtUser) authentication.getPrincipal();

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

    @RequestMapping(value = REFRESH_END_POINT, method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        final JwtToken currentToken = new JwtToken(request.getHeader(property.getSecurity().getJwt().getController().getTokenHeader()));
        final JwtToken newToken = this.authenticationRestService.refreshAndGetAuthenticationToken(currentToken);
        cacheAuthService.refreshToken(currentToken, newToken);
        return ResponseEntity.ok(newToken);
    }

    /**
     * O Metodo é notificado toda vez que um token é gerado para um determinado usuário
     *
     * @param user  -> usuário do token gerado
     * @param token -> token gerado
     */
    protected void afterGeneratedToken(final JwtUser user, final JwtToken token) {
    }

    private final void checkPayloadContainsUserNameAndPasswdOndy(final UserPayLoadLogin payload) {
        if (isEmpty(payload.getUsername()) || isEmpty(payload.getPassword())) {
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
