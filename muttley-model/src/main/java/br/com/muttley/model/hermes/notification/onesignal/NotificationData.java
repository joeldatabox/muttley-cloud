package br.com.muttley.model.hermes.notification.onesignal;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author Joel Rodrigues Moreira on 04/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

@Getter
@Setter
@Accessors(chain = true)
public class NotificationData {
    @NotBlank
    private String type;
    private Object payload;
    private NotificationEventType notificationEventType;
}

