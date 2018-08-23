package br.com.muttley.security.properties;

/**
 * @author Joel Rodrigues Moreira on 22/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
public enum Module {
    GATEWAY,
    CLIENT;

    public boolean isGateway() {
        return this.equals(GATEWAY);
    }

    public boolean isClient() {
        return this.equals(CLIENT);
    }
}
