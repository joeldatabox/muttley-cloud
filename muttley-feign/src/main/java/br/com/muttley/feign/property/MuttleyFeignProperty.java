package br.com.muttley.feign.property;

import feign.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MuttleyFeignProperty.PREFIX)
public class MuttleyFeignProperty {
    protected static final String PREFIX = "muttley.config.feign";
    private Loggin loggin = new Loggin();
    private int connectTimeOutMillis = 60000;
    private int readTimeOutMillis = 60000;

    public int getConnectTimeOutMillis() {
        return connectTimeOutMillis;
    }

    public MuttleyFeignProperty setConnectTimeOutMillis(int connectTimeOutMillis) {
        this.connectTimeOutMillis = connectTimeOutMillis;
        return this;
    }

    public int getReadTimeOutMillis() {
        return readTimeOutMillis;
    }

    public MuttleyFeignProperty setReadTimeOutMillis(int readTimeOutMillis) {
        this.readTimeOutMillis = readTimeOutMillis;
        return this;
    }

    public Loggin getLoggin() {
        return loggin;
    }

    public MuttleyFeignProperty setLoggin(Loggin loggin) {
        this.loggin = loggin;
        return this;
    }

    public static class Loggin {
        private Logger.Level level;

        public Logger.Level getLevel() {
            return level;
        }

        public Loggin setLevel(Logger.Level level) {
            this.level = level;
            return this;
        }
    }
}
