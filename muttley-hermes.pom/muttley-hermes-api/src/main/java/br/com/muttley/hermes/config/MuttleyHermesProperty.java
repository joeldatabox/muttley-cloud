package br.com.muttley.hermes.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Joel Rodrigues Moreira on 02/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@ConfigurationProperties(prefix = MuttleyHermesProperty.PREFIX)
public class MuttleyHermesProperty {
    protected static final String PREFIX = "muttley.hermes.server";
    private String name = "hermes-server";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
