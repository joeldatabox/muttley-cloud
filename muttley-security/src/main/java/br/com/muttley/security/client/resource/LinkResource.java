package br.com.muttley.security.client.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public class LinkResource {
    private final String rel;
    private final String href;

    @JsonCreator
    public LinkResource(
            @JsonProperty("rel") final String rel,
            @JsonProperty("href") final String href) {
        this.rel = rel;
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public String getHref() {
        return href;
    }
}
