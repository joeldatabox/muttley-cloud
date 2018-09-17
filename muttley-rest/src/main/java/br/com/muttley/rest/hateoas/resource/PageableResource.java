package br.com.muttley.rest.hateoas.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public final class PageableResource implements Serializable {

    private final List records;
    private final MetadataPageable _metadata;

    public PageableResource(final List records, final MetadataPageable _metadata) {
        this.records = records;
        this._metadata = _metadata;
    }

    public List getRecords() {
        return records;
    }

    public MetadataPageable get_metadata() {
        return _metadata;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(this.records);
    }
}
