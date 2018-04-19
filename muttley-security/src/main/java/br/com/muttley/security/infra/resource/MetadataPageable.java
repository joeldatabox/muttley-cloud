package br.com.muttley.security.infra.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public class MetadataPageable {
    private final Long page;
    private final Long pageSize;
    private final Long totalPages;
    private final Long totalRecords;
    private final Long totalRecordsInPage;
    private final List<LinkResource> links;

    @JsonCreator
    public MetadataPageable(
            @JsonProperty("page") final Long page,
            @JsonProperty("pageSize") final Long pageSize,
            @JsonProperty("totalPages") final Long totalPages,
            @JsonProperty("totalRecords") final Long totalRecords,
            @JsonProperty("totalRecordsInPage") final Long totalRecordsInPage,
            @JsonProperty("links") final List<LinkResource> links) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalRecords = totalRecords;
        this.totalRecordsInPage = totalRecordsInPage;
        this.links = links;
    }

    public Long getPage() {
        return page;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public Long getTotalRecordsInPage() {
        return totalRecordsInPage;
    }

    public List<LinkResource> getLinks() {
        return links;
    }
}
