package br.com.muttley.notification.onesignal.model.events;

import br.com.muttley.notification.onesignal.model.Notification;
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
