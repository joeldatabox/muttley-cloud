package br.com.muttley.model.hermes.notification.onesignal.events;

import br.com.muttley.model.hermes.notification.onesignal.Notification;
import org.springframework.context.ApplicationEvent;

public class NotificationEvent extends ApplicationEvent {
    public NotificationEvent(final Notification notification) {
        super(notification);
    }

    @Override
    public Notification getSource() {
        return (Notification) super.getSource();
    }
}
