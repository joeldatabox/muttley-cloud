package br.com.muttley.notification.onesignal.model.events;

import br.com.muttley.notification.onesignal.model.Notification;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 05/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class NotificationEvent extends ApplicationEvent {
    public NotificationEvent(final Notification notification) {
        super(notification);
    }

    @Override
    public Notification getSource() {
        return (Notification) super.getSource();
    }
}
