package br.com.muttley.hermes.server.liteners;

import br.com.muttley.notification.onesignal.model.Content;
import br.com.muttley.notification.onesignal.model.Notification;
import br.com.muttley.notification.onesignal.model.NotificationData;
import br.com.muttley.notification.onesignal.model.events.NotificationEvent;
import br.com.muttley.notification.onesignal.service.OneSignalNotificationService;
import br.com.muttley.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static br.com.muttley.notification.onesignal.model.MuttleyLanguage.English;

@Component
public class NotificationEventListener implements ApplicationListener<NotificationEvent> {
    private static final String KEY_REDIS = "muttley-notification-cache";
    private final RedisService redisService;
    private final OneSignalNotificationService oneSignalService;

    @Autowired
    public NotificationEventListener(final RedisService redisService, final OneSignalNotificationService oneSignalService) {
        this.redisService = redisService;
        this.oneSignalService = oneSignalService;
        this.oneSignalService.sendNotification(new Notification().setAppId("f9f00ebb-1d73-4e6b-a127-b6a086f48111")
                .addPlayers("6c9e98ff-2542-4278-848b-1310abd57b04")
                .setData(new NotificationData().setType("asdfasd").setPayload("asdfasdf"))
                .addContent(new Content(English, "English"))
                .addHeadings(new Content(English, "headings"))
                .addSubtitles(new Content(English, "subtitles"))
        );
    }

    @Override
    public void onApplicationEvent(final NotificationEvent notificationEvent) {
        this.oneSignalService.sendNotification(notificationEvent.getSource());
    }
}
