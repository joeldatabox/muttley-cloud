package br.com.muttley.model.hermes.notification.twilio;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Joel Rodrigues Moreira on 26/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class SMSPayload {
    private String to;
    private String body;
}
