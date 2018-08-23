package br.com.muttley.security.infra.component;

import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.security.properties.MuttleySecurityProperty;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
@Component
public class UnauthorizedHandler implements AuthenticationEntryPoint, Serializable {

    @Autowired
    private MuttleySecurityProperty property;

    @Override
    public void commence(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setHeader(RESPONSE_HEADER, RESPONSE_HEADER_VALUE);
        response.getWriter()
                .print(new MuttleySecurityUnauthorizedException("Unauthorized!")
                        .addDetails("urlLogin", request
                                .getRequestURL()
                                .toString()
                                .replace(request.getRequestURI(), "") + property.getSecurity().getJwt().getController().getLoginEndPoint()
                        ).toJson());

    }
}