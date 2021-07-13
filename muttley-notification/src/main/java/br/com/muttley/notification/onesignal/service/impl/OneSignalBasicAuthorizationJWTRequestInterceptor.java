package br.com.muttley.notification.onesignal.service.impl;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;

public class OneSignalBasicAuthorizationJWTRequestInterceptor implements RequestInterceptor {
    private final String tokenHeader;
    private final String tokenValue;

    public OneSignalBasicAuthorizationJWTRequestInterceptor(
            @Value("${muttley.notification.onesignal.tokenHeader:Authorization}") final String tokenHeader,
            @Value("${muttley.notification.onesignal.tokenValue:#null}") final String tokenValue) {
        this.tokenHeader = tokenHeader;
        this.tokenValue = tokenValue;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(this.tokenHeader, this.tokenValue);
    }
}
