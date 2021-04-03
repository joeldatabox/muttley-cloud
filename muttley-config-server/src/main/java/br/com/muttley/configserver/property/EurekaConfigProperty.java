package br.com.muttley.configserver.property;

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
@ConfigurationProperties(prefix = EurekaConfigProperty.PREFIX)
@Getter
@Setter
@Accessors(chain = true)
public class EurekaConfigProperty {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected static final String PREFIX = "eureka";
    private Client client = new Client();

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Client {
        private String region = "default";

        public int registryFetchIntervalSeconds = 10;
    }
}
