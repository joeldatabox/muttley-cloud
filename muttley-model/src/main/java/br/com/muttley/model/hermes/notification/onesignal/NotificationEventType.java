package br.com.muttley.model.hermes.notification.onesignal;

import br.com.muttley.model.hermes.notification.onesignal.jackson.NotificationEnventTypeSerializer;
import br.com.muttley.model.hermes.notification.onesignal.jackson.NotificationEnventTypedeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Joel Rodrigues Moreira 28/09/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@JsonSerialize(using = NotificationEnventTypeSerializer.class)
@JsonDeserialize(using = NotificationEnventTypedeserializer.class)
public interface NotificationEventType {
    String getType();
}
