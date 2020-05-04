package br.com.muttley.notification.onesignal.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class NotificationData {
    private String type;
    private Object payload;
}
