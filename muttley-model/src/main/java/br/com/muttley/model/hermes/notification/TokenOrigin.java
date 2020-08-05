package br.com.muttley.model.hermes.notification;

import org.springframework.util.StringUtils;

/**
 * @author Joel Rodrigues Moreira on 03/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public enum TokenOrigin {
    OneSignal;

    public static TokenOrigin getTokenOrigin(final String origin) {
        if (StringUtils.isEmpty(origin)) {
            return null;
        }
        return "onesignal".equalsIgnoreCase(origin) ? OneSignal : null;
    }
}

