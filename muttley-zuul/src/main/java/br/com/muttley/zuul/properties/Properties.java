package br.com.muttley.zuul.properties;

/**
 * @author Joel Rodrigues Moreira on 03/09/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
public class Properties {
    public static final String TOKEN_HEADER_JWT ="${muttley.security.jwt.controller.token-header-jwt:Authorization-jwt}";
    public static final String TOKEN_HEADER = "${muttley.security.jwt.controller.token-header:Authorization}";
}
