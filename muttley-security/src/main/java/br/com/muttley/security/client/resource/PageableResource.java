package br.com.muttley.security.client.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public final class PageableResource implements Serializable {

    private final List records;
    private final MetadataPageable _metadata;

    @JsonCreator
    public PageableResource(
            @JsonProperty("records") final List records,
            @JsonProperty("_metadata") final MetadataPageable _metadata) {
        this.records = records;
        this._metadata = _metadata;
    }

    public List getRecords() {
        return records;
    }

    public MetadataPageable get_metadata() {
        return _metadata;
    }
}
