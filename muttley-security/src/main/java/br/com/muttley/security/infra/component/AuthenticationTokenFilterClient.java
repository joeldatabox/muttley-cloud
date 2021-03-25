package br.com.muttley.security.infra.component;

import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.localcache.services.LocalUserAuthenticationService;
import br.com.muttley.model.security.JwtToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Aplica o filtro de autenticação necessario
 */
public class AuthenticationTokenFilterClient extends OncePerRequestFilter {

    private final String tokenHeader;
    private final LocalUserAuthenticationService localUserAuthentication;


    public AuthenticationTokenFilterClient(@Value("${muttley.security.jwt.controller.tokenHeader-jwt:Authorization-jwt}") final String tokenHeader, final LocalUserAuthenticationService localUserAuthentication) {
        this.tokenHeader = tokenHeader;
        this.localUserAuthentication = localUserAuthentication;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws ServletException, IOException {
        //recuperando o possivel token presente no cabeçalho
        final String authToken = request.getHeader(this.tokenHeader);

        if (!isNullOrEmpty(authToken)) {

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    final UserDetails userDetails = this.localUserAuthentication.getJwtUserFrom(new JwtToken(authToken));
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
