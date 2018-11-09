package br.com.muttley.security.infra.server;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;

import static br.com.muttley.security.infra.properties.Properties.TOKEN_HEADER;
import static br.com.muttley.security.infra.properties.Properties.TOKEN_HEADER_JWT;
import static org.springframework.util.Base64Utils.encode;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 17/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Classe básica para tramento de segurança dentro do ecossitema.
 */
public class BasicAuthorizationJWTRequestInterceptor implements RequestInterceptor {
    private static final Charset CHARSET = Charset.forName("ISO-8859-1");
    private final String headerValue;
    private final String tokenHeaderJwt;
    private final String tokenHeader;

    public BasicAuthorizationJWTRequestInterceptor(final String username, final String password, @Value(TOKEN_HEADER_JWT) final String tokenHeaderJwt, @Value(TOKEN_HEADER) final String tokenHeader) {
        Util.checkNotNull(username, "username", new Object[0]);
        Util.checkNotNull(password, "password", new Object[0]);
        this.headerValue = base64Encode(username + ":" + password);
        this.tokenHeaderJwt = tokenHeaderJwt;
        this.tokenHeader = tokenHeader;
    }

    private static String base64Encode(String userPasswd) {
        return "Basic " + new String(encode(userPasswd.getBytes(CHARSET)), CHARSET);
    }


    @Override
    public void apply(final RequestTemplate template) {
        template.header(this.tokenHeader, this.headerValue);
        try {
            final String AUTH = this.getAuthorizationJWT();
            if (!isEmpty(AUTH)) {
                template.header(this.tokenHeaderJwt, AUTH);
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
        final String jwtToken = request.getHeader(this.tokenHeaderJwt);
        if (!isEmpty(jwtToken)) {
            return jwtToken.equals("null") ? null : jwtToken;
        }
        //se chegou até aqui quer dizer que ninguem ainda não fez esse tratamento
        //devemos pegar o token no "Authorization"
        return request.getHeader(this.tokenHeader);
    }
}