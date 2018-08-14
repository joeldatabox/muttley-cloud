package br.com.muttley.redis.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MuttleyRedisProperty.PREFIX)
public class MuttleyRedisProperty {
    protected static final String PREFIX = "muttley.redis";
    private String host;
    private int port;
    private String prefixHash;

    public MuttleyRedisProperty() {
        this.host = "localhost";
        this.port = 6379;
        this.prefixHash = "muttley-cloud";
    }

    public String getHost() {
        return host;
    }

    public MuttleyRedisProperty setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public MuttleyRedisProperty setPort(int port) {
        this.port = port;
        return this;
    }

    public String getPrefixHash() {
        return prefixHash;
    }

    public MuttleyRedisProperty setPrefixHash(String prefixHash) {
        this.prefixHash = prefixHash;
        return this;
    }
}
