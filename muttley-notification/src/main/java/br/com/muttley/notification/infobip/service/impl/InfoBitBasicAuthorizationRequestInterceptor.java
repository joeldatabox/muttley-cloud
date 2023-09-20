package br.com.muttley.notification.infobip.service.impl;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Joel Rodrigues Moreira on 18/09/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class InfoBitBasicAuthorizationRequestInterceptor implements RequestInterceptor {
    private final String AUTHENTICATION_TOKEN;

    public InfoBitBasicAuthorizationRequestInterceptor(@Value("${muttley.notification.infobip.token}") final String token) {
        this.AUTHENTICATION_TOKEN = token;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", this.AUTHENTICATION_TOKEN);
    }
}
