package br.com.muttley.notification.infobip.service.impl;

import br.com.muttley.model.hermes.notification.infobip.Messages;
import br.com.muttley.model.hermes.notification.infobip.SMSPayload;
import br.com.muttley.notification.infobip.service.InfoBitNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira on 18/09/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class InfoBitNotificationServiceImpl implements InfoBitNotificationService {
    private final InfoBitNotificationServiceClient client;

    @Autowired
    public InfoBitNotificationServiceImpl(InfoBitNotificationServiceClient client) {
        this.client = client;
    }

    @Override
    public void sendNotification(SMSPayload payload) {
        this.client.sendSMS(payload);
    }
}
