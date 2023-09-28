package br.com.muttley.notification.onesignal.service.impl;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class OneSignalBasicAuthorizationJWTRequestInterceptor implements RequestInterceptor {
    private final String tokenHeader;
    private final String tokenValue;
    private final Logger log;

    public OneSignalBasicAuthorizationJWTRequestInterceptor(
            @Value("${muttley.notification.onesignal.tokenHeader:Authorization}") final String tokenHeader,
            @Value("${muttley.notification.onesignal.tokenValue:#null}") final String tokenValue) {
        this.tokenHeader = tokenHeader;
        this.tokenValue = tokenValue;

        if ("#null".equalsIgnoreCase(tokenValue)) {
            this.log = LoggerFactory.getLogger(OneSignalBasicAuthorizationJWTRequestInterceptor.class);
            this.log.error("\n\n NÃO FOI INFORMADO UM TOKEN VÁLIDO PARA O ONESIGNAL \n\n");
        } else {
            this.log = null;
        }
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(this.tokenHeader, this.tokenValue);
        if ("#null".equalsIgnoreCase(tokenValue)) {
            this.log.error("\n\n NÃO FOI INFORMADO UM TOKEN VÁLIDO PARA O ONESIGNAL \n\n");
        }
    }
}
