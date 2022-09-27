package br.com.muttley.notification.twilio.service.impl;

import br.com.muttley.model.hermes.notification.twilio.SMSPayload;
import br.com.muttley.notification.twilio.service.TwilioNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Joel Rodrigues Moreira on 26/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class TwilioNotificationServiceImpl implements TwilioNotificationService {
    private final TwilioNotificationServiceClient client;
    private final String serviceSid;

    @Autowired
    public TwilioNotificationServiceImpl(@Value("${muttley.notification.twilio.MessagingServiceSid}") final String serviceSid, TwilioNotificationServiceClient client) {
        this.client = client;
        this.serviceSid = serviceSid;
    }

    @Override
    public void sendNotification(SMSPayload payload) {
        //setando o sid;
        payload.setServiceSid(this.serviceSid);
        this.client.sendNotification(payload);
    }
}
