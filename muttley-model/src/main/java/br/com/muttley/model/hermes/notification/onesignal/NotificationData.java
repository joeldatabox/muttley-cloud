package br.com.muttley.model.hermes.notification.onesignal;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
@Accessors(chain = true)
public class NotificationData {
    @NotBlank
    private String type;
    private Object payload;
    private NotificationEventType notificationEventType;
}
