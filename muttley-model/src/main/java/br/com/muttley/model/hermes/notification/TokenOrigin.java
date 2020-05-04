package br.com.muttley.model.hermes.notification;

import br.com.muttley.model.hermes.notification.jackson.TokenOriginDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.util.StringUtils;

@JsonDeserialize(using = TokenOriginDeserializer.class)
public enum TokenOrigin {
    OneSignal;

    public static TokenOrigin getTokenOrigin(final String origin) {
        if (StringUtils.isEmpty(origin)) {
            return null;
        }
        return "OneSignal".equalsIgnoreCase(origin) ? OneSignal : null;
    }
}
