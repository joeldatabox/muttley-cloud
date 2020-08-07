package br.com.muttley.notification.onesignal.service;

import br.com.muttley.model.hermes.notification.onesignal.Notification;

/**
 * @author Joel Rodrigues Moreira on 02/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

public interface OneSignalNotificationService {
    public void sendNotification(final Notification notification);
}
