package br.com.muttley.security.server.components;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static br.com.muttley.security.server.components.RSAPairKeyProperty.PREFIX;

@ConfigurationProperties(prefix = PREFIX)
@Getter
@Setter
@Accessors(chain = true)
public class RSAPairKeyProperty {
    protected static final String PREFIX = "muttley.rsa";

    private String privateKeyFile = "rsa/id_rsa_sfa";
    private String publicKeyFile = "rsa/id_rsa_sfa.pub";
    private String seed;
    private boolean autoCriateIfNotExists = true;

}
