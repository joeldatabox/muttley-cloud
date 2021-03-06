package br.com.muttley.hermes.server.service;

import br.com.muttley.model.hermes.notification.onesignal.Content;
import br.com.muttley.model.hermes.notification.onesignal.Notification;
import br.com.muttley.model.security.UserView;

/**
 * @author Joel Rodrigues Moreira on 06/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface NotificationService {
    void sendNotification(final Notification notification);

    void sendNotification(final String playerId, final Notification notification);

    void sendNotification(final UserView user, final Notification notification);

    void sendNotificationByUserId(final String userId, final Notification notification);

    void sendNotificationMobile(final UserView user, final Notification notification);

    void sendNotificationMobileByUserId(final String userId, final Notification notification);

    void sendNotification(final UserView user, final Content headings, final Content subtitles, final Content contents);

    void sendNotificationByUserId(final String userId, final Content headings, final Content subtitles, final Content contents);

    void sendNotificationMobile(final UserView user, final Content headings, final Content subtitles, final Content contents);

    void sendNotificationMobileByUserId(final String userId, final Content headings, final Content subtitles, final Content contents);

    void sendNotification(final UserView user, final String heading, final String subtitle, final String content);

    void sendNotificationByUserId(final String userId, final String heading, final String subtitle, final String content);

    void sendNotificationMobile(final UserView user, final String heading, final String subtitle, final String content);

    void sendNotificationMobileByUserId(final String userId, final String heading, final String subtitle, final String content);
}
