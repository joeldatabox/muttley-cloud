package br.com.muttley.model.hermes.notification;

import org.springframework.util.StringUtils;

public enum TokenOrigin {
    OneSignal;

    public static TokenOrigin getTokenOrigin(final String origin) {
        if (StringUtils.isEmpty(origin)) {
            return null;
        }
        return "onesignal".equalsIgnoreCase(origin) ? OneSignal : null;
    }
}
