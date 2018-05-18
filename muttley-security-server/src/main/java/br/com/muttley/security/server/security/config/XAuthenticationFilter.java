package br.com.muttley.security.server.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.nio.charset.Charset.forName;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 17/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class XAuthenticationFilter extends OncePerRequestFilter {

    private static final String X_AUTHORIZATION = "X-Authorization";
    private final AuthenticationManager authenticationManager;

    @Autowired
    public XAuthenticationFilter(final AuthenticationManager authenticationManager) {

        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final FilterChain filterChain) throws ServletException, IOException {


        final String xAuth = httpServletRequest.getHeader(X_AUTHORIZATION);
        if (!isEmpty(xAuth)) {


            final String userPasswd = new String(Base64.decode(xAuth.getBytes(forName("ISO-8859-1"))));
            final String user = userPasswd.split(":")[0];
            final String passWord = userPasswd.split(":")[1];

            //gerando a authenticação do serviço
            final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user, passWord));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        //dispachando a requisição
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
