package br.com.muttley.files.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static br.com.muttley.files.properties.Properties.PREFIX;

/**
 * @author Joel Rodrigues Moreira on 28/07/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

@ConfigurationProperties(prefix = PREFIX)
@Getter
@Setter
@Accessors(chain = true)
public class Properties {
    public static final String PREFIX = "muttley-cloud";

    private String files = "muttley-cloud-files";
}
