package br.com.muttley.notification.onesignal.service;

import br.com.muttley.notification.onesignal.model.Notification;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient(url = "https://onesignal.com",value = "${muttley.onesignal.domain:https://onesignal.com}", path = "/api/v1", configuration = {OneSignalBasicAuthorizationJWTRequestInterceptor.class})
public interface OneSignalNotificationService {
    @RequestMapping(value = "/notifications", method = POST, produces = APPLICATION_JSON_VALUE)
    public void sendNotification(@RequestBody Notification token);
}
