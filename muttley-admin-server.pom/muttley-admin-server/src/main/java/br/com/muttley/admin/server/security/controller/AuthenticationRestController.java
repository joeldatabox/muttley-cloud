package br.com.muttley.admin.server.security.controller;

import br.com.muttley.localcache.services.LocalUserAuthenticationService;
import br.com.muttley.security.feign.auth.AuthenticationRestServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
public class AuthenticationRestController extends br.com.muttley.security.infra.controller.AuthenticationRestController {

    @Autowired
    public AuthenticationRestController(
            @Value("${muttley.security.jwt.controller.tokenHeader:Authorization}") final String tokenHeader,
            final AuthenticationManager authenticationManager,
            final AuthenticationRestServiceClient authenticationRestServiceClient,
            final ApplicationEventPublisher eventPublisher,
            final LocalUserAuthenticationService localUserAuthenticationService) {
        super(tokenHeader, authenticationManager, authenticationRestServiceClient, eventPublisher, localUserAuthenticationService);
    }
}
