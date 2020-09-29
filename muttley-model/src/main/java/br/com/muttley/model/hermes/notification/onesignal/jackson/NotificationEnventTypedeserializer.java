package br.com.muttley.model.hermes.notification.onesignal.jackson;

import br.com.muttley.model.hermes.notification.onesignal.NotificationEventType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import static br.com.muttley.model.hermes.notification.onesignal.NotificationEventTypeAvaliable.getNotificationEventType;

/**
 * @author Joel Rodrigues Moreira 28/09/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class NotificationEnventTypedeserializer extends JsonDeserializer<NotificationEventType> {
    @Override
    public NotificationEventType deserialize(final JsonParser parser, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final JsonNode node = parser.getCodec().readTree(parser);
        return getNotificationEventType(node.asText());
    }
}
