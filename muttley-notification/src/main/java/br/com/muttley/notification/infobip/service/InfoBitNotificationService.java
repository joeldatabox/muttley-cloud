package br.com.muttley.notification.infobip.service;

import br.com.muttley.model.hermes.notification.infobip.SMSPayload;

/**
 * @author Joel Rodrigues Moreira on 18/09/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface InfoBitNotificationService {
    void sendNotification(final SMSPayload payload);
}
