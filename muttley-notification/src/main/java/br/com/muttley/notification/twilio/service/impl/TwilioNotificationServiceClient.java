package br.com.muttley.notification.twilio.service.impl;

import br.com.muttley.notification.twilio.config.TwilioConfig;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joel Rodrigues Moreira on 26/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(url = "${muttley.notification.twilio.url:https://api.twilio.com}",
        value = "twilio",
        configuration = {TwilioConfig.class, TwilioBasicAuthorizationRequestInterceptor.class})
public interface TwilioNotificationServiceClient {

    //@RequestMapping(method = POST, produces = APPLICATION_FORM_URLENCODED_VALUE)
    @RequestLine("POST")
    @Headers("Type: application/x-www-form-urlencoded")
    void sendNotification(Map<String, String> value);
}
