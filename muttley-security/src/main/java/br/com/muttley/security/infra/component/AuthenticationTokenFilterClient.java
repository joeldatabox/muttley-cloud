package br.com.muttley.security.infra.component;

import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.localcache.services.LocalUserAuthenticationService;
import br.com.muttley.localcache.services.LocalXAPITokenService;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.XAPIToken;
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
 * Aplica o filtro de autenticação necessario
 */
public class AuthenticationTokenFilterClient extends OncePerRequestFilter {

    private final String tokenHeader;
    protected final String xAPIToken;
    private final LocalUserAuthenticationService localUserAuthentication;
    protected final LocalXAPITokenService apiTokenService;


    public AuthenticationTokenFilterClient(@Value("${muttley.security.jwt.controller.tokenHeader-jwt:Authorization-jwt}") final String tokenHeader, @Value("${muttley.security.jwt.controller.xAPITokenHeader:X-Api-Token}") final String xAPIToken, final LocalUserAuthenticationService localUserAuthentication, LocalXAPITokenService apiTokenService) {
        this.tokenHeader = tokenHeader;
        this.xAPIToken = xAPIToken;
        this.localUserAuthentication = localUserAuthentication;
        this.apiTokenService = apiTokenService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws ServletException, IOException {
        //recuperando o possivel token presente no cabeçalho
        final String authToken = request.getHeader(this.tokenHeader);
        final String xAPIToken = request.getHeader(this.xAPIToken);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            if (!isNullOrEmpty(authToken)) {
                this.loadJWTUser(request, authToken);
            } else if (!isNullOrEmpty(xAPIToken)) {
                this.loadXAPIToken(request, xAPIToken);
            }
        }
        //dispachando a requisição
        chain.doFilter(request, response);
    }

    private void loadXAPIToken(HttpServletRequest request, String xAPIToken) {
        XAPIToken XAPIToken = this.apiTokenService.loadAPIToken(xAPIToken);
        if (XAPIToken != null) {
            final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(XAPIToken, null, XAPIToken.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private void loadJWTUser(final HttpServletRequest request, final String authToken) {
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
