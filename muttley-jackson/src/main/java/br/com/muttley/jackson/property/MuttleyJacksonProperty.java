package br.com.muttley.jackson.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MuttleyJacksonProperty.PREFIX)
public class MuttleyJacksonProperty {
    protected static final String PREFIX = "br.com.muttley.jackson";
    private String datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public String getDatePattern() {
        return datePattern;
    }

    public MuttleyJacksonProperty setDatePattern(String datePattern) {
        this.datePattern = datePattern;
        return this;
    }
}
