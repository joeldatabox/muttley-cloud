package br.com.muttley.muttleydiscoveryserver.property;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Joel Rodrigues Moreira on 02/04/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@ConfigurationProperties(prefix = SpringConfigProperty.PREFIX)
@Getter
@Setter
@Accessors(chain = true)
public class SpringConfigProperty {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected static final String PREFIX = "spring";

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Application {
        private String name = "muttley-discovery-server";
    }

}
