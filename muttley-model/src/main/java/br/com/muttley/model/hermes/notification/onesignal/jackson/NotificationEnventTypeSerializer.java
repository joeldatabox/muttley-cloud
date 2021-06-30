package br.com.muttley.model.hermes.notification.onesignal.jackson;

import br.com.muttley.model.hermes.notification.onesignal.NotificationEventType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 29/06/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class NotificationEnventTypeSerializer extends JsonSerializer<NotificationEventType> {
    @Override
    public void serialize(final NotificationEventType notificationEventType, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        if (notificationEventType == null) {
            gen.writeNull();
        } else {
            gen.writeString(notificationEventType.getType());
        }
    }
}
