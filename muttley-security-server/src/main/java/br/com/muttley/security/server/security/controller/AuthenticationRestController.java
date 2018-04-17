package br.com.muttley.security.server.security.controller;

import br.com.muttley.security.infra.component.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController responsavel por despachar novas requisições de token
 *
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@RestController
public class AuthenticationRestController extends br.com.muttley.security.infra.controller.AuthenticationRestController {

    @Autowired
    public AuthenticationRestController(
            @Value("${muttley.security.jwt.controller.tokenHeader:Authorization}") final String tokenHeader,
            final AuthenticationManager authenticationManager,
            final JwtTokenUtil jwtTokenUtil,
            final UserDetailsService userDetailsService,
            final ApplicationEventPublisher eventPublisher) {
        super(tokenHeader, authenticationManager, jwtTokenUtil, userDetailsService, eventPublisher);
    }
}
