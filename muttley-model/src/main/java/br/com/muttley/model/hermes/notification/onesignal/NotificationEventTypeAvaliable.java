package br.com.muttley.model.hermes.notification.onesignal;

import br.com.muttley.model.hermes.notification.onesignal.jackson.NotificationEnventTypeSerializer;
import br.com.muttley.model.hermes.notification.onesignal.jackson.NotificationEnventTypedeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.stream.Stream;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira 28/09/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@JsonSerialize(using = NotificationEnventTypeSerializer.class)
@JsonDeserialize(using = NotificationEnventTypedeserializer.class)
public enum NotificationEventTypeAvaliable implements NotificationEventType {
    CREATE,
    READ,
    UPDATE,
    DELETE;

    public static NotificationEventType getNotificationEventType(final String event) {
        if (isEmpty(event)) {
            return null;
        }
        return Stream.of(NotificationEventTypeAvaliable.values())
                .parallel()
                .filter(n -> n.name().equalsIgnoreCase(event))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getType() {
        return this.name();
    }

    @Override
    public String toString() {
        return this.getType();
    }
}
