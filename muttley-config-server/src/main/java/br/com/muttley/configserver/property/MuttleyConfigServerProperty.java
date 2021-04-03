package br.com.muttley.configserver.property;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MuttleyConfigServerProperty.PREFIX)
@Getter
@Setter
public class MuttleyConfigServerProperty {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected static final String PREFIX = "muttley.config-server";
    private Security security = new Security();

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Security {
        private User user = new User();

        @Getter
        @Setter
        @Accessors(chain = true)
        public static class User {
            private String name = "muttley";
            private String password = "muttley";
            private String role = "ROLE_SYSTEM";
        }
    }
}
