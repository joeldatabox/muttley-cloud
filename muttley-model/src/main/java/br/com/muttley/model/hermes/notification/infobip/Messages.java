package br.com.muttley.model.hermes.notification.infobip;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 18/09/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class Messages {
    private Set<Destination> destinations;
    private String from;
    private String text;
}
