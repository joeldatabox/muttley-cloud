package br.com.muttley.security.infra.component;

import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

import static br.com.muttley.exception.ErrorMessage.RESPONSE_HEADER;
import static br.com.muttley.exception.ErrorMessage.RESPONSE_HEADER_VALUE;
import static br.com.muttley.security.infra.properties.MuttleySecurityProperties.LOGIN_END_POINT;

/**
 * @author Joel Rodrigues Moreira on 30/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Componete responsável por fazer o devido tratamento caso alguma resição feita não tenha
 * sido autenticada devidamente
 * </p>
 */
@Component
public class UnauthorizedHandler implements AuthenticationEntryPoint, Serializable {
    private final String loginEndpoin;

    @Autowired
    public UnauthorizedHandler(@Value(LOGIN_END_POINT) String loginEndpoin) {
        this.loginEndpoin = loginEndpoin;
    }

    @Override
    public void commence(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(RESPONSE_HEADER, RESPONSE_HEADER_VALUE);
        response.getWriter()
                .print(new MuttleySecurityUnauthorizedException("Unauthorized!")
                        .addDetails("urlLogin", request
                                .getRequestURL()
                                .toString()
                                .replace(request.getRequestURI(), "") + loginEndpoin
                        ).toJson());

    }
}
