package br.com.muttley.notification.onesignal.service.impl;

import br.com.muttley.model.hermes.notification.onesignal.Notification;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient(url = "${muttley.notification.onesignal.domain:https://onesignal.com}", value = "onesignal", path = "/api/v1", configuration = {OneSignalBasicAuthorizationJWTRequestInterceptor.class})
interface OneSignalNotificationServiceClient {
    @RequestMapping(value = "/notifications", method = POST, produces = APPLICATION_JSON_VALUE)
    public void sendNotification(@RequestBody Notification token);
}
