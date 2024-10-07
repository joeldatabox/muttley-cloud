package br.com.muttley.admin.server.security.config;

import br.com.muttley.security.feign.UserServiceClient;
import br.com.muttley.security.infra.component.AuthenticationTokenFilterGateway;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import br.com.muttley.security.zuul.gateway.AbstractWebSecurityGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityGatewayConfig extends AbstractWebSecurityGateway {

    @Autowired
    public WebSecurityGatewayConfig(
            @Value("${muttley.security.jwt.controller.loginEndPoint}") final String loginEndPoint,
            @Value("${muttley.security.jwt.controller.refreshEndPoint}") final String refreshTokenEndPoin,
            @Value("${muttley.security.jwt.controller.forgotPassword}") final String forgotPassword,
            @Value("${muttley.security.jwt.controller.createEndPoint}") final String createEndPoint,
            @Value("${muttley.security.jwt.controller.resetPassword}") final String resetPassword,
            final UnauthorizedHandler unauthorizedHandler,
            final AuthenticationTokenFilterGateway authenticationTokenFilterGateway,
            final UserServiceClient userServiceClient) {
        super(loginEndPoint, refreshTokenEndPoin, forgotPassword, createEndPoint, resetPassword, unauthorizedHandler, authenticationTokenFilterGateway, userServiceClient);
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
