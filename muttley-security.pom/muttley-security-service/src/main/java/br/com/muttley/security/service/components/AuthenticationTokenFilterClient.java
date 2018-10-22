package br.com.muttley.security.service.components;

import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.security.infra.services.CacheUserAuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static br.com.muttley.security.infra.properties.Properties.TOKEN_HEADER_JWT;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Aplica o filtro de autenticação necessario
 */
@Component
public class AuthenticationTokenFilterClient extends OncePerRequestFilter {
    private final String tokenHeader;
    private final CacheUserAuthenticationService cacheAuth;


    public AuthenticationTokenFilterClient(final @Value(TOKEN_HEADER_JWT) String tokenHeader, final CacheUserAuthenticationService cacheAuth) {
        this.tokenHeader = tokenHeader;
        this.cacheAuth = cacheAuth;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws ServletException, IOException {
        //recuperando o possivel token presente no cabeçalho
        final String authToken = request.getHeader(this.tokenHeader);

        if (!isNullOrEmpty(authToken)) {

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    //buscando o usuário no cache
                    final UserDetails userDetails = this.cacheAuth.get(authToken);

                    //verificando a validade do token
                    if (userDetails != null) {
                        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (MuttleySecurityUnauthorizedException ex) {
                }
            }
        }
        //dispachando a requisição
        chain.doFilter(request, response);
    }
}
