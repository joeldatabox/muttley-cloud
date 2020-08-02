package br.com.muttley.notification.onesignal.configuration;


import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Joel Rodrigues Moreira on 02/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@ConfigurationProperties(prefix = MuttleyOneSignalProperty.PREFIX)
public class MuttleyOneSignalProperty {
    protected static final String PREFIX = "muttley.onesignal";
    private String tokenHeader = "Authorization";
    private String tokenValue;
    private String domain = "https://onesignal.com";

    public String getTokenHeader() {
        return tokenHeader;
    }

    public void setTokenHeader(String tokenHeader) {
        this.tokenHeader = tokenHeader;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
