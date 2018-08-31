package br.com.muttley.security.gateway.components;

import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;


import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.security.infra.feign.auth.AuthenticationTokenServiceClient;
import br.com.muttley.security.infra.properties.MuttleySecurityProperties;


import br.com.muttley.security.infra.services.CacheUserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
public class AuthenticationTokenFilterGateway extends OncePerRequestFilter {

    @Autowired
    private MuttleySecurityProperties property;
    protected final AuthenticationTokenServiceClient tokenServiceClient;
    protected final CacheUserAuthenticationService cacheAuth;
    protected final ApplicationEventPublisher eventPublisher;

    @Autowired
    public AuthenticationTokenFilterGateway(final AuthenticationTokenServiceClient userServiceClient, final CacheUserAuthenticationService cacheAuth, final ApplicationEventPublisher eventPublisher) {
        this.tokenServiceClient = userServiceClient;
        this.cacheAuth = cacheAuth;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws ServletException, IOException {
        //recuperando o possivel token presente no cabeçalho
        final String authToken = request.getHeader(property.getSecurity().getJwt().getController().getTokenHeader());

        if (!isNullOrEmpty(authToken)) {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    //buscando o usuário presente no token
                    final JwtUser userDetails = this.tokenServiceClient.getUserFromToken(new JwtToken(authToken));


                    final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    if (!this.cacheAuth.contains(authToken)) {
                        //salvando no cache
                        this.cacheAuth.set(authToken, userDetails);
                    }
                } catch (MuttleySecurityUnauthorizedException ex) {

                }
            }
        }
        //dispachando a requisição
        chain.doFilter(request, response);

    }
}
