package br.com.muttley.model.hermes.notification;

import br.com.muttley.model.hermes.notification.jackson.TokenOriginDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.util.StringUtils;

/**
 * @author Joel Rodrigues Moreira on 03/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@JsonDeserialize(using = TokenOriginDeserializer.class)
public enum TokenOrigin {
    OneSignal;

    public static TokenOrigin getTokenOrigin(final String origin) {
        if (StringUtils.isEmpty(origin)) {
            return null;
        }
        return OneSignal.name().equalsIgnoreCase(origin) ? OneSignal : null;
    }
}

