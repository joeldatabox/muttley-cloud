package br.com.muttley.notification.onesignal.service;

import br.com.muttley.model.hermes.notification.onesignal.Notification;

public interface OneSignalNotificationService {
    public void sendNotification(final Notification notification);
}
