package br.com.muttley.hermes.api;

import br.com.muttley.feign.autoconfig.FeignTimeoutConfig;
import br.com.muttley.model.hermes.notification.onesignal.Notification;
import br.com.muttley.security.infra.server.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joel Rodrigues Moreira on 06/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.hermes.server.name}", path = "/api/v1/tokens-notification", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class})
public interface NotificationClient {
    @RequestMapping(method = POST, consumes = APPLICATION_JSON_VALUE)
    public void sendNotification(@RequestBody final Notification notification);

    @RequestMapping(value = "/{playerId}", method = POST, consumes = APPLICATION_JSON_VALUE)
    public void sendNotification(@PathVariable("playerId") final String playerId, @RequestBody final Notification notification);

    @RequestMapping(value = "/send-by-user/{userId}", method = POST, consumes = APPLICATION_JSON_VALUE)
    public void sendNotificationByUserId(@PathVariable("userId") final String userId, @RequestBody final Notification notification);

    @RequestMapping(value = "/send-by-mobile-user/{userId}", method = POST, consumes = APPLICATION_JSON_VALUE)
    public void sendNotificationMobileByUser(@PathVariable("userId") final String userId, @RequestBody final Notification notification);

    @RequestMapping(value = "/simple-send-by-user/{userId}", method = POST, consumes = APPLICATION_JSON_VALUE)
    public void sendNotificationByUser(@PathVariable("userId") final String userId, @PathVariable(value = "heading", required = false) final String heading, @PathVariable(value = "subtitle", required = false) final String subtitle, @PathVariable(value = "content", required = false) final String content);

    @RequestMapping(value = "/simple-send-by-mobile-user/{userId}", method = POST, consumes = APPLICATION_JSON_VALUE)
    public void sendNotificationMobileByUserId(@PathVariable("userId") final String userId, @PathVariable(value = "heading", required = false) final String heading, @PathVariable(value = "subtitle", required = false) final String subtitle, @PathVariable(value = "content", required = false) final String content);
}
