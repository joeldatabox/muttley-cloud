package br.com.muttley.rest.service.listenerEventsHateoas;


import br.com.muttley.rest.hateoas.event.PaginatedResultsRetrievedEvent;
import br.com.muttley.rest.util.LinkUtil;
import com.google.common.base.Preconditions;
import com.google.common.net.HttpHeaders;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import static br.com.muttley.rest.util.LinkUtil.createLinkHeader;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
@Component
public class PaginatedResultsRetrievedDiscoverabilityListener implements ApplicationListener<PaginatedResultsRetrievedEvent> {
    private static final String PAGE = "page";

    @Override
    public void onApplicationEvent(final PaginatedResultsRetrievedEvent event) {
        Preconditions.checkNotNull(event);

        final StringBuilder linkHeader = new StringBuilder();
        if (hasNextPage(event.getPage(), event.getTotalPages())) {
            final String uriForNextPage = constructNextPageUri(event.getUriBuilder(), event.getPage(), event.getPageSize());
            linkHeader.append(createLinkHeader(uriForNextPage, LinkUtil.REL_NEXT));
        }
        if (hasPreviousPage(event.getPage())) {
            final String uriForPrevPage = constructPrevPageUri(event.getUriBuilder(), event.getPage(), event.getPageSize());
            appendCommaIfNecessary(linkHeader);
            linkHeader.append(createLinkHeader(uriForPrevPage, LinkUtil.REL_PREV));
        }
        if (hasFirstPage(event.getPage())) {
            final String uriForFirstPage = constructFirstPageUri(event.getUriBuilder(), event.getPageSize());
            appendCommaIfNecessary(linkHeader);
            linkHeader.append(createLinkHeader(uriForFirstPage, LinkUtil.REL_FIRST));
        }
        if (hasLastPage(event.getPage(), event.getTotalPages())) {
            final String uriForLastPage = constructLastPageUri(event.getUriBuilder(), event.getTotalPages(), event.getPageSize());
            appendCommaIfNecessary(linkHeader);
            linkHeader.append(createLinkHeader(uriForLastPage, LinkUtil.REL_LAST));
        }

        if (linkHeader.length() > 0) {
            event.getResponse().addHeader(HttpHeaders.LINK, linkHeader.toString());
        }
    }

    final String constructNextPageUri(final UriComponentsBuilder uriBuilder, final long page, final long size) {
        return uriBuilder.replaceQueryParam(PAGE, page + 1).replaceQueryParam("size", size).build().encode().toUriString();
    }

    final String constructPrevPageUri(final UriComponentsBuilder uriBuilder, final long page, final long size) {
        return uriBuilder.replaceQueryParam(PAGE, page - 1).replaceQueryParam("size", size).build().encode().toUriString();
    }

    final String constructFirstPageUri(final UriComponentsBuilder uriBuilder, final long size) {
        return uriBuilder.replaceQueryParam(PAGE, 0).replaceQueryParam("size", size).build().encode().toUriString();
    }

    final String constructLastPageUri(final UriComponentsBuilder uriBuilder, final long totalPages, final long size) {
        return uriBuilder.replaceQueryParam(PAGE, totalPages).replaceQueryParam("size", size).build().encode().toUriString();
    }

    final boolean hasNextPage(final long page, final long totalPages) {
        return page < (totalPages - 1);
    }

    final boolean hasPreviousPage(final long page) {
        return page > 0;
    }

    final boolean hasFirstPage(final long page) {
        return hasPreviousPage(page);
    }

    final boolean hasLastPage(final long page, final long totalPages) {
        return (totalPages > 1) && hasNextPage(page, totalPages);
    }

    final void appendCommaIfNecessary(final StringBuilder linkHeader) {
        if (linkHeader.length() > 0) {
            linkHeader.append(", ");
        }
    }
}
