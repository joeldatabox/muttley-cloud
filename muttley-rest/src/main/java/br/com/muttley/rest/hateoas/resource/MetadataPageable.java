package br.com.muttley.rest.hateoas.resource;

import br.com.muttley.rest.util.LinkUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public class MetadataPageable {
    private Long page;
    private Long pageSize;
    private Long totalPages;
    private Long totalRecords;
    private Long totalRecordsInPage;
    private final List<LinkResource> links;
    @JsonIgnore
    private Long limit;
    @JsonIgnore
    private Long skip;
    @JsonIgnore
    private static final String SKIP = "$skip";
    @JsonIgnore
    private static final String LIMIT = "$limit";

    public MetadataPageable() {
        this.links = new ArrayList<>();
    }

    public MetadataPageable(final UriComponentsBuilder componentsBuilder, final Long limit, final Long skip, final Long pageSize, final Long totalRecords) {
        this();
        this.totalRecords = totalRecords;
        Double totalPages = Math.floor(totalRecords / limit);
        if (totalRecords % limit != 0.0) {
            totalPages++;
        }
        this.totalPages = totalPages.longValue();
        this.pageSize = pageSize;
        if (skip == 0) {
            this.page = 1L;
        } else {
            this.page = (skip + limit) / limit;
        }
        this.skip = skip;
        this.limit = limit;
        addFirstPage(componentsBuilder);
        addPreviusPage(componentsBuilder);
        addNextPage(componentsBuilder);
        addLastPage(componentsBuilder);
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

    public List<LinkResource> getLinks() {
        return links;
    }

    private void addNextPage(final UriComponentsBuilder componentsBuilder) {
        if (containsNextPage()) {
            links.add(new LinkResource(
                    LinkUtil.REL_NEXT,
                    componentsBuilder
                            .replaceQueryParam(SKIP, skip + limit)
                            .replaceQueryParam(LIMIT, limit)
                            .build()
                            //.encode()
                            .toUriString()
            ));

        }
    }

    @JsonIgnore
    public boolean containsNextPage() {
        return (skip + limit) < totalRecords;
    }

    @JsonIgnore
    public LinkResource getNextPage() {
        return this.links.stream()
                .filter(l -> LinkUtil.REL_NEXT.equals(l.getRel()))
                .findFirst()
                .get();
    }

    private void addPreviusPage(final UriComponentsBuilder componentsBuilder) {
        if (containsPreviusPage()) {
            links.add(new LinkResource(
                    LinkUtil.REL_PREV,
                    componentsBuilder
                            .replaceQueryParam(SKIP, skip - limit)
                            .replaceQueryParam(LIMIT, limit)
                            .build()
                            //.encode()
                            .toUriString()
            ));
        }
    }

    @JsonIgnore
    public boolean containsPreviusPage() {
        return page > 1;
    }

    @JsonIgnore
    public LinkResource getPreviusPage() {
        return this.links.stream()
                .filter(l -> LinkUtil.REL_PREV.equals(l.getRel()))
                .findFirst()
                .get();
    }

    private void addFirstPage(final UriComponentsBuilder componentsBuilder) {
        if (containsFirstPage()) {
            links.add(new LinkResource(
                    "first",
                    componentsBuilder
                            .replaceQueryParam(SKIP, 0)
                            .replaceQueryParam(LIMIT, limit)
                            .build()
                            //.encode()
                            .toUriString()
            ));
        }
    }

    @JsonIgnore
    public boolean containsFirstPage() {
        return page > 1;
    }

    @JsonIgnore
    public LinkResource getFirstPage() {
        return this.links.stream()
                .filter(l -> LinkUtil.REL_FIRST.equals(l.getRel()))
                .findFirst()
                .get();
    }

    private void addLastPage(final UriComponentsBuilder componentsBuilder) {
        if (containsLastPage()) {
            //final Long skip = String.valueOf(limit * (totalPages - 1)).substring(0,)
            long skip = 0;
            if (skip > 9) {
                String last = String.valueOf((skip + limit));
                if (last.length() > 1) {
                    last = last.substring(last.length() - 1, last.length());
                }
                String first = String.valueOf(limit * (totalPages - 1));
                if (first.length() > 1) {
                    first = first.substring(0, first.length() - 1);
                }
                skip = Long.valueOf(first + last);
            } else {
                skip = limit * (totalPages - 1);
            }
            links.add(new LinkResource(
                    LinkUtil.REL_LAST,
                    componentsBuilder
                            //.replaceQueryParam(SKIP, limit * (totalPages - 1))
                            .replaceQueryParam(SKIP, skip)
                            .replaceQueryParam(LIMIT, limit)
                            .build()
                            //.encode()
                            .toUriString()
            ));
        }
    }

    @JsonIgnore
    public boolean containsLastPage() {
        return (skip + limit) < totalRecords;
    }

    @JsonIgnore
    public LinkResource getLastPage() {
        return this.links.stream()
                .filter(l -> LinkUtil.REL_LAST.equals(l.getRel()))
                .findFirst()
                .get();
    }
}
