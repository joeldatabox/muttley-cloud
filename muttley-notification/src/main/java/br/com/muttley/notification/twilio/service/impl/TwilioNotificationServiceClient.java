package br.com.muttley.notification.twilio.service.impl;

import br.com.muttley.notification.onesignal.service.impl.OneSignalBasicAuthorizationJWTRequestInterceptor;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * @author Joel Rodrigues Moreira on 26/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(url = "${muttley.notification.onesignal.domain:https://onesignal.com}", value = "onesignal", path = "/api/v1", configuration = {TwilioBasicAuthorizationRequestInterceptor.class})
public interface TwilioNotificationServiceClient {

    void sendNotification();
}
