package br.com.muttley.hermes.server.liteners;

import br.com.muttley.model.hermes.notification.onesignal.events.NotificationEvent;
import br.com.muttley.notification.onesignal.service.OneSignalNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener implements ApplicationListener<NotificationEvent> {
    private final OneSignalNotificationService oneSignalService;

    @Autowired
    public NotificationEventListener(final OneSignalNotificationService oneSignalService) {
        this.oneSignalService = oneSignalService;
    }

    @Override
    public void onApplicationEvent(final NotificationEvent notificationEvent) {
        this.oneSignalService.sendNotification(notificationEvent.getSource());
    }
}
