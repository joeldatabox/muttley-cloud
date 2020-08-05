package br.com.muttley.notification.onesignal.service;

import br.com.muttley.notification.onesignal.model.Notification;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joel Rodrigues Moreira on 02/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

@FeignClient(url = "${muttley.onesignal.domain}", value = "${muttley.onesignal.domain}", path = "/api/v1", configuration = {OneSignalBasicAuthorizationJWTRequestInterceptor.class})
public interface OneSignalNotificationService {
    @RequestMapping(value = "/notifications", method = POST, produces = APPLICATION_JSON_VALUE)
    public void sendNotification(@RequestBody Notification token);
}
