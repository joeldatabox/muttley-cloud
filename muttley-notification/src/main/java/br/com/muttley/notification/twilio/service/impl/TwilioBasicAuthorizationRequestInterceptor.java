package br.com.muttley.notification.twilio.service.impl;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.crypto.codec.Base64;

import java.nio.charset.Charset;

/**
 * @author Joel Rodrigues Moreira on 26/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class TwilioBasicAuthorizationRequestInterceptor implements RequestInterceptor {
    private static final Charset CHARSET = Charset.forName("ISO-8859-1");
    private final String AUTHENTICATION;

    public TwilioBasicAuthorizationRequestInterceptor(final String user, final String password) {
        this.AUTHENTICATION = base64Encode(user + ":" + password);
    }

    private static String base64Encode(String payload) {
        return "Basic " + new String(Base64.encode(payload.getBytes(CHARSET)), CHARSET);
    }


    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", this.AUTHENTICATION);
    }
}
