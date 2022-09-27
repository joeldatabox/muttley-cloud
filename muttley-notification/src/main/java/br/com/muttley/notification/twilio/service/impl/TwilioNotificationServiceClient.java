package br.com.muttley.notification.twilio.service.impl;

import br.com.muttley.model.hermes.notification.twilio.SMSPayload;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joel Rodrigues Moreira on 26/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(url = "${muttley.notification.twilio.domain:https://api.twilio.com}",
        value = "twilio",
        configuration = {TwilioBasicAuthorizationRequestInterceptor.class})
public interface TwilioNotificationServiceClient {

    @RequestMapping(method = POST, consumes = APPLICATION_FORM_URLENCODED_VALUE)
    void sendNotification(final SMSPayload payload);
}
