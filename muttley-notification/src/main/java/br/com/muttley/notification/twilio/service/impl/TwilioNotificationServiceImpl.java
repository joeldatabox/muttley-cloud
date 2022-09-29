package br.com.muttley.notification.twilio.service.impl;

import br.com.muttley.model.hermes.notification.twilio.SMSPayload;
import br.com.muttley.notification.twilio.service.TwilioNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 26/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class TwilioNotificationServiceImpl implements TwilioNotificationService {
    private final String serviceSid;

    @Autowired
    private TwilioNotificationServiceClient client;

    @Autowired
    public TwilioNotificationServiceImpl(@Value("${muttley.notification.twilio.serviceId}") final String serviceSid) {
        this.serviceSid = serviceSid;
    }

    @Override
    public void sendNotification(SMSPayload payload) {
        payload.setServiceSid(this.serviceSid);

        final Map<String, String> maps = new HashMap<>();
        maps.put("To", payload.getTo());
        maps.put("MessagingServiceSid", payload.getServiceSid());
        maps.put("Body", payload.getBody());

        this.client.sendNotification(maps);


    }
}
