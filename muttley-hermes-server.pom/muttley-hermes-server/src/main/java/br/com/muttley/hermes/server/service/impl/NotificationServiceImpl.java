package br.com.muttley.hermes.server.service.impl;

import br.com.muttley.domain.service.Validator;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.hermes.server.service.NotificationService;
import br.com.muttley.hermes.server.service.UserTokensNotificationService;
import br.com.muttley.model.security.UserView;
import br.com.muttley.model.hermes.notification.onesignal.Content;
import br.com.muttley.model.hermes.notification.onesignal.Notification;
import br.com.muttley.notification.onesignal.service.OneSignalNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.muttley.model.hermes.notification.onesignal.MuttleyLanguage.Any;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final Validator validator;
    private final OneSignalNotificationService oneSignalNotificationServiceClient;
    private final UserTokensNotificationService userTokensNotificationService;


    @Autowired
    public NotificationServiceImpl(final Validator validator, final OneSignalNotificationService oneSignalNotificationServiceClient, final UserTokensNotificationService userTokensNotificationService) {
        this.validator = validator;
        this.oneSignalNotificationServiceClient = oneSignalNotificationServiceClient;
        this.userTokensNotificationService = userTokensNotificationService;
    }

    @Override
    public void sendNotification(final Notification notification) {
        this.validator.validate(notification);
        this.oneSignalNotificationServiceClient.sendNotification(notification);
    }

    @Override
    public void sendNotification(final String playerId, final Notification notification) {
        this.sendNotification(notification.addPlayers(playerId));
    }

    @Override
    public void sendNotification(final UserView user, final Notification notification) {
        try {
            this.sendNotification(notification.addPlayers(this.userTokensNotificationService.findByUser(user)));
        } catch (final MuttleyNotFoundException ex) {
        }
    }

    @Override
    public void sendNotificationByUserId(final String userId, final Notification notification) {
        this.sendNotification(new UserView().setId(userId), notification);
    }

    @Override
    public void sendNotificationMobile(final UserView user, final Notification notification) {
        try {
            this.sendNotification(
                    notification.addPlayers(
                            this.userTokensNotificationService
                                    .findByUser(user)
                                    .getTokensMobile()
                    )
            );
        } catch (final MuttleyNotFoundException ex) {
        }
    }

    @Override
    public void sendNotificationMobileByUserId(final String userId, final Notification notification) {
        this.sendNotificationMobile(new UserView().setId(userId), notification);
    }

    @Override
    public void sendNotification(final UserView user, final Content headings, final Content subtitles, final Content contents) {
        this.sendNotification(user, new Notification().addHeadings(headings).addSubtitles(subtitles).addContent(contents));
    }

    @Override
    public void sendNotificationByUserId(final String userId, final Content headings, final Content subtitles, final Content contents) {
        this.sendNotification(new UserView().setId(userId), headings, subtitles, contents);
    }

    @Override
    public void sendNotificationMobile(final UserView user, final Content headings, final Content subtitles, final Content contents) {
        this.sendNotificationMobile(user, new Notification().addHeadings(headings).addSubtitles(subtitles).addContent(contents));
    }

    @Override
    public void sendNotificationMobileByUserId(final String userId, final Content headings, final Content subtitles, final Content contents) {
        this.sendNotificationMobile(new UserView().setId(userId), headings, subtitles, contents);
    }

    @Override
    public void sendNotification(final UserView user, final String heading, final String subtitle, final String content) {
        this.sendNotification(user, new Content(Any, heading), new Content(Any, subtitle), new Content(Any, content));
    }

    @Override
    public void sendNotificationByUserId(final String userId, final String heading, final String subtitle, final String content) {
        this.sendNotification(new UserView().setId(userId), heading, subtitle, content);
    }

    @Override
    public void sendNotificationMobile(final UserView user, final String heading, final String subtitle, final String content) {
        this.sendNotificationMobile(user, new Content(Any, heading), new Content(Any, subtitle), new Content(Any, content));
    }

    @Override
    public void sendNotificationMobileByUserId(final String userId, final String heading, final String subtitle, final String content) {
        this.sendNotificationMobile(new UserView().setId(userId), heading, subtitle, content);
    }
}
