package br.com.muttley.security.server.security.config;

import br.com.muttley.security.infra.component.AuthenticationTokenFilterGateway;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import br.com.muttley.security.zuul.gateway.AbstractWebSecurityGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityGatewayConfig extends AbstractWebSecurityGateway {

    @Autowired
    public WebSecurityGatewayConfig(
            @Value("${muttley.security.jwt.controller.loginEndPoint}") final String loginEndPoint,
            @Value("${muttley.security.jwt.controller.refreshEndPoint}") final String refreshTokenEndPoin,
            @Value("${muttley.security.jwt.controller.createEndPoint}") final String createEndPoint,
            final UnauthorizedHandler unauthorizedHandler,
            final UserDetailsService userDetailsService,
            final AuthenticationTokenFilterGateway authenticationTokenFilterGateway) {
        super(loginEndPoint, refreshTokenEndPoin, createEndPoint, unauthorizedHandler, userDetailsService, authenticationTokenFilterGateway);
    }

    @Override
    protected String[] endPointPermitAllToGet() {
        return new String[]{
                "/",
                "/*.html",
                "/**/*.{png,jpg,jpeg,svg.ico}",
                "/**/*.{html,css,js,svg,woff,woff2}",
                //endpoit padrão da aplicação
                "/login",
                "/create-user",
                "/home/**"};
    }
}
