package br.com.muttley.security.infra.security.server;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Util;

import java.nio.charset.Charset;

/**
 * @author Joel Rodrigues Moreira on 17/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class XBasicAuthRequestInterceptor implements RequestInterceptor {
    private final String headerValue;

    public XBasicAuthRequestInterceptor(final String username, final String password) {
        Util.checkNotNull(username, "username", new Object[0]);
        Util.checkNotNull(password, "password", new Object[0]);
        this.headerValue = "Basic " + base64Encode((username + ":" + password).getBytes(Charset.forName("ISO-8859-1")));
    }

    private static String base64Encode(byte[] bytes) {
        return new String(bytes, Charset.forName("ISO-8859-1"));
    }


    @Override
    public void apply(final RequestTemplate template) {
        template.header("X-Authorization", new String[]{this.headerValue});
    }
}