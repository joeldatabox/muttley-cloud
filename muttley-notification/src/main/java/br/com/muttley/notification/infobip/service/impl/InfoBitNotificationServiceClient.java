package br.com.muttley.notification.infobip.service.impl;

import br.com.muttley.model.hermes.notification.infobip.SMSPayload;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joel Rodrigues Moreira on 18/09/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(url = "${muttley.notification.infobip.url:https://5y83wx.api.infobip.com/sms/2/text/advanced}", value = "infobit", configuration = {InfoBitBasicAuthorizationRequestInterceptor.class})
public interface InfoBitNotificationServiceClient {
    @RequestMapping(method = POST, consumes = APPLICATION_JSON_UTF8_VALUE)
    void sendSMS(SMSPayload payload);
}
