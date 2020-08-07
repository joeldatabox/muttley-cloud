package br.com.muttley.metadata.headers;

import br.com.muttley.model.security.JwtToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Joel Rodrigues Moreira on 02/11/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Component
@RequestScope
public class HeaderAuthorizationJWT extends HeaderMuttley {

    public HeaderAuthorizationJWT(@Value("${muttley.security.jwt.controller.token-header-jwt:Authorization-jwt}") String tokenHeader, HttpServletRequest request) {
        super(tokenHeader, request);
    }

    public JwtToken getToken() {
        return new JwtToken(this.currentValue);
    }

}
