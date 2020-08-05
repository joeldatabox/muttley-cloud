package br.com.muttley.notification.onesignal.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Joel Rodrigues Moreira on 04/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

@Getter
@Setter
@Accessors(chain = true)
public class NotificationData {
    private String type;
    private Object payload;
}

