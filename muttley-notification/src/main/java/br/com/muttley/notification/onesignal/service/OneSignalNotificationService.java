package br.com.muttley.notification.onesignal.service;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.notification.onesignal.model.Notification;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joel Rodrigues Moreira on 02/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

@FeignClient(value = "${muttley.onesignal.domain:https://onesignal.com}", path = "/api/v1", configuration = {OneSignalBasicAuthorizationJWTRequestInterceptor.class})
public interface OneSignalNotificationService {
    @RequestMapping(value = "/notifications", method = POST)
    public JwtToken refreshAndGetAuthenticationToken(@RequestBody Notification token);
}
