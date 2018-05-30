package br.com.muttley.security.infra.security.server;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 17/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class BasicAuthorizationJWTRequestInterceptor implements RequestInterceptor {
    private static final Charset CHARSET = Charset.forName("ISO-8859-1");
    //${muttley.security.jwt.client.tokenHeader:Athorization-JWT}
    @Value("${muttley.security.jwt.controller.tokenHeader-jwt:Authorization-jwt}")
    private String authorizationJwt;
    @Value("${muttley.security.jwt.controller.tokenHeader:Authorization}")
    private String authorization;
    private final String headerValue;

    public BasicAuthorizationJWTRequestInterceptor(final String username, final String password) {
        Util.checkNotNull(username, "username", new Object[0]);
        Util.checkNotNull(password, "password", new Object[0]);
        this.headerValue = base64Encode(username + ":" + password);
    }

    private static String base64Encode(String userPasswd) {
        return "Basic " + new String(Base64.encode(userPasswd.getBytes(CHARSET)), CHARSET);
    }


    @Override
    public void apply(final RequestTemplate template) {
        template.header(authorization, this.headerValue);
        try {
            final String AUTH = this.getAuthorizationJWT();
            if (!isEmpty(AUTH)) {
                template.header(authorizationJwt, AUTH);
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Deve retornar o token do usuário corrente na requisição
     */
    private String getAuthorizationJWT() {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        //Talvez a requisão já advem de outro subserviço, ou seja já contem no header o "Authorization-jwt"
        final String jwtToken = request.getHeader(this.authorizationJwt);
        if (!isEmpty(jwtToken)) {
            return jwtToken;
        }
        //se chegou até aqui quer dizer que ninguem ainda não fez esse tratamento
        //devemos pegar o token no "Authorization"
        return request.getHeader(this.authorization);
    }
}