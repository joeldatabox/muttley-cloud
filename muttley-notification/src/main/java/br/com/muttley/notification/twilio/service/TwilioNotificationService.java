package br.com.muttley.notification.twilio.service;

import br.com.muttley.model.hermes.notification.twilio.SMSPayload;

/**
 * @author Joel Rodrigues Moreira on 26/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface TwilioNotificationService {
    void sendNotification(final SMSPayload payload);
}
