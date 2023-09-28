package br.com.muttley.notification.twilio.service.impl;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Base64;

import java.nio.charset.Charset;

/**
 * @author Joel Rodrigues Moreira on 26/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class TwilioBasicAuthorizationRequestInterceptor implements RequestInterceptor {
    private static final Charset CHARSET = Charset.forName("ISO-8859-1");
    private final String user;
    private final String password;
    private final String AUTHENTICATION;
    private final Logger log;

    public TwilioBasicAuthorizationRequestInterceptor(@Value("${muttley.notification.twilio.accountSid:INVALID}") final String user, @Value("${muttley.notification.twilio.accountToken:INVALID}") final String password) {
        this.user = user;
        this.password = password;
        this.AUTHENTICATION = base64Encode(user + ":" + password);
        if ("INVALID".equalsIgnoreCase(user) || "INVALID".equalsIgnoreCase(password)) {
            this.log = LoggerFactory.getLogger(TwilioBasicAuthorizationRequestInterceptor.class);
            this.log.error("\n\n NÃO FOI INFORMADO USUÁRIO OU SENHA VÁLIDO PARA O TWILIO \n\n");
        } else {
            this.log = null;
        }
    }

    private static String base64Encode(String payload) {
        return "Basic " + new String(Base64.encode(payload.getBytes(CHARSET)), CHARSET);
    }


    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", this.AUTHENTICATION);
        if ("INVALID".equalsIgnoreCase(this.user) || "INVALID".equalsIgnoreCase(this.password)) {
            this.log.error("\n\n NÃO FOI INFORMADO USUÁRIO OU SENHA VÁLIDO PARA O TWILIO \n\n");
        }
    }
}
