package br.com.muttley.notification.onesignal.service.impl;

import br.com.muttley.model.hermes.notification.onesignal.Notification;
import br.com.muttley.notification.onesignal.service.OneSignalNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira on 06/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class OneSignalNotificationServiceImpl implements OneSignalNotificationService {
    private final OneSignalNotificationServiceClient oneSignalNotificationServiceClient;
    private final String APP_ID;

    @Autowired
    public OneSignalNotificationServiceImpl(final OneSignalNotificationServiceClient oneSignalNotificationServiceClient, @Value("${muttley.notification.onesignal.appId}") final String APP_ID) {
        this.oneSignalNotificationServiceClient = oneSignalNotificationServiceClient;
        this.APP_ID = APP_ID;
    }

    @Override
    public void sendNotification(final Notification notification) {
        this.oneSignalNotificationServiceClient.sendNotification(notification.setAppId(APP_ID));
    }
}

