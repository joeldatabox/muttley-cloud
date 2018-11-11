package br.com.muttley.security.infra.component;

import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.security.infra.feign.auth.AuthenticationTokenServiceClient;
import br.com.muttley.security.infra.services.CacheUserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static br.com.muttley.security.infra.properties.Properties.TOKEN_HEADER;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
@Component
public class AuthenticationTokenFilterGateway extends OncePerRequestFilter {
    @Value(TOKEN_HEADER)
    protected String tokenHeader;
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
        final String authToken = request.getHeader(tokenHeader);

        if (!isNullOrEmpty(authToken)) {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    JwtUser userDetails;
                    //verificando se já existe esse usuário no cache
                    if (this.cacheAuth.contains(authToken)) {
                        userDetails = this.cacheAuth.get(authToken);
                    } else {
                        //não existe vamos buscar o mesmo no serviço
                        userDetails = this.tokenServiceClient.getUserFromToken(new JwtToken(authToken));
                        //salvando o usuário no cache
                        cacheAuth.set(authToken, userDetails);
                    }

                    final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (MuttleySecurityUnauthorizedException ex) {

                }
            }
        }
        //dispachando a requisição
        chain.doFilter(request, response);

    }
}
