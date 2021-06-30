package br.com.muttley.model.hermes.notification.onesignal;

import br.com.muttley.model.hermes.notification.onesignal.jackson.NotificationEnventTypeSerializer;
import br.com.muttley.model.hermes.notification.onesignal.jackson.NotificationEnventTypeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Joel Rodrigues Moreira on 29/06/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@JsonSerialize(using = NotificationEnventTypeSerializer.class)
@JsonDeserialize(using = NotificationEnventTypeDeserializer.class)
public interface NotificationEventType {
    String getType();
}
