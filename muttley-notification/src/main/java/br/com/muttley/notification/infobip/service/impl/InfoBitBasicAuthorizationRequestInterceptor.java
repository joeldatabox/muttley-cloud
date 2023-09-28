package br.com.muttley.notification.infobip.service.impl;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Joel Rodrigues Moreira on 18/09/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class InfoBitBasicAuthorizationRequestInterceptor implements RequestInterceptor {
    private final String AUTHENTICATION_TOKEN;
    private final Logger log;


    public InfoBitBasicAuthorizationRequestInterceptor(@Value("${muttley.notification.infobip.token:INVALID}") final String token) {
        this.AUTHENTICATION_TOKEN = token;
        if ("INVALID".equalsIgnoreCase(token)) {
            this.log = LoggerFactory.getLogger(InfoBitBasicAuthorizationRequestInterceptor.class);
            this.log.error("\n\n NÃO FOI INFORMADO UM TOKEN VÁLIDO PARA O INFOBIT \n\n");
        } else {
            this.log = null;
        }
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", this.AUTHENTICATION_TOKEN);
        if ("INVALID".equalsIgnoreCase(this.AUTHENTICATION_TOKEN)) {
            this.log.error("\n\n NÃO FOI INFORMADO UM TOKEN VÁLIDO PARA O INFOBIT \n\n");
        }
    }
}
