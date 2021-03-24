package br.com.muttley.security.infra.component;

import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.security.infra.service.LocalUserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    protected final String tokenHeader;
    protected final LocalUserAuthenticationService localUserAuthentication;

    @Autowired
    public AuthenticationTokenFilterGateway(
            @Value("${muttley.security.jwt.controller.tokenHeader:Authorization}") final String tokenHeader,
            final LocalUserAuthenticationService localUserAuthentication) {
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
                    //buscando o usuário presente no token
                    //final JwtUser userDetails = this.tokenServiceClient.getUserFromToken(new JwtToken(authToken));
                    final JwtUser userDetails = this.localUserAuthentication.getJwtUserFrom(new JwtToken(authToken));
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
