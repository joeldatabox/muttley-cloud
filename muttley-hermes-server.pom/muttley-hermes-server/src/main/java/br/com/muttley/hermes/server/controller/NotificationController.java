package br.com.muttley.hermes.server.controller;

import br.com.muttley.hermes.server.service.NotificationService;
import br.com.muttley.model.hermes.notification.onesignal.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/api/v1/notifications", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RequestMapping(method = POST)
    public void sendNotification(@RequestBody final Notification notification) {
        this.notificationService.sendNotification(notification);
    }

    @RequestMapping(value = "/{playerId}", method = POST)
    public void sendNotification(@PathVariable("playerId") final String playerId, @RequestBody final Notification notification) {
        this.notificationService.sendNotification(notification.addPlayers(playerId));
    }

    @RequestMapping(value = "/send-by-user/{userId}", method = POST)
    public void sendNotificationByUserId(@PathVariable("userId") final String userId, @RequestBody final Notification notification) {
        this.notificationService.sendNotificationByUserId(userId, notification);
    }

    @RequestMapping(value = "/send-by-mobile-user/{userId}", method = POST)
    public void sendNotificationMobileByUser(@PathVariable("userId") final String userId, @RequestBody final Notification notification) {
        this.notificationService.sendNotificationMobileByUserId(userId, notification);
    }

    @RequestMapping(value = "/simple-send-by-user/{userId}", method = POST)
    public void sendNotificationByUser(@PathVariable("userId") final String userId, @PathVariable(value = "heading", required = false) final String heading, @PathVariable(value = "subtitle", required = false) final String subtitle, @PathVariable(value = "content", required = false) final String content) {
        this.notificationService.sendNotificationByUserId(userId, heading, subtitle, content);
    }

    @RequestMapping(value = "/simple-send-by-mobile-user/{userId}", method = POST)
    public void sendNotificationMobileByUserId(@PathVariable("userId") final String userId, @PathVariable(value = "heading", required = false) final String heading, @PathVariable(value = "subtitle", required = false) final String subtitle, @PathVariable(value = "content", required = false) final String content) {
        this.notificationService.sendNotificationMobileByUserId(userId, heading, subtitle, content);
    }
}
