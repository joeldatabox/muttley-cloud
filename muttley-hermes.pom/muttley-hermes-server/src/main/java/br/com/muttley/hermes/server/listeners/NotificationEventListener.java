package br.com.muttley.hermes.server.listeners;

import br.com.muttley.model.hermes.notification.onesignal.events.NotificationEvent;
import br.com.muttley.notification.onesignal.service.OneSignalNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 05/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
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
