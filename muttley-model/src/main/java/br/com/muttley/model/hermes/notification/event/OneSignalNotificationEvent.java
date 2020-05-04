package br.com.muttley.model.hermes.notification.event;

import br.com.muttley.model.hermes.notification.UserTokensNotification;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 01/05/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OneSignalNotificationEvent extends ApplicationEvent {

    public OneSignalNotificationEvent(UserTokensNotification token) {
        super(token);
    }
}
