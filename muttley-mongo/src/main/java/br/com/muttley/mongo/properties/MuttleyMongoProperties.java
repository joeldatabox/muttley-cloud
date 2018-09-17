package br.com.muttley.mongo.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Joel Rodrigues Moreira on 26/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@ConfigurationProperties(prefix = MuttleyMongoProperties.PREFIX)
public class MuttleyMongoProperties {
    protected static final String PREFIX = "muttley.mongo";
    private MuttleyMongoStrategy strategy = MuttleyMongoStrategy.SimpleTenancy;

    public MuttleyMongoStrategy getStrategy() {
        return strategy;
    }

    public MuttleyMongoProperties setStrategy(MuttleyMongoStrategy strategy) {
        this.strategy = strategy;
        return this;
    }
}
