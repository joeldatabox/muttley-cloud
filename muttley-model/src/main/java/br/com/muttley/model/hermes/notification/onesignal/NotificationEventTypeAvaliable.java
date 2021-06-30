package br.com.muttley.model.hermes.notification.onesignal;

import br.com.muttley.model.hermes.notification.onesignal.jackson.NotificationEnventTypeSerializer;
import br.com.muttley.model.hermes.notification.onesignal.jackson.NotificationEnventTypeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.stream.Stream;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 29/06/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@JsonSerialize(using = NotificationEnventTypeSerializer.class)
@JsonDeserialize(using = NotificationEnventTypeDeserializer.class)
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
