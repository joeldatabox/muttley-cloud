package br.com.muttley.rest.service.listenerEventsHateoas;


import br.com.muttley.rest.hateoas.event.PaginatedResultsRetrievedEvent;
import br.com.muttley.rest.hateoas.resource.MetadataPageable;
import br.com.muttley.rest.util.LinkUtil;
import com.google.common.base.Preconditions;
import com.google.common.net.HttpHeaders;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
@Component
public class PaginatedResultsRetrievedDiscoverabilityListener implements ApplicationListener<PaginatedResultsRetrievedEvent> {

    @Override
    public void onApplicationEvent(final PaginatedResultsRetrievedEvent event) {
        Preconditions.checkNotNull(event);
        final MetadataPageable metadataPageable = event.getMetadataPageable();
        final StringBuilder linkHeader = new StringBuilder();
        if (event.getMetadataPageable().containsNextPage()) {
            linkHeader.append(LinkUtil.createLinkHeader(metadataPageable.getNextPage().getHref(), LinkUtil.REL_NEXT));
        }
        if (metadataPageable.containsPreviusPage()) {
            appendCommaIfNecessary(linkHeader);
            linkHeader.append(LinkUtil.createLinkHeader(metadataPageable.getPreviusPage().getHref(), LinkUtil.REL_PREV));
        }
        if (metadataPageable.containsFirstPage()) {
            appendCommaIfNecessary(linkHeader);
            linkHeader.append(LinkUtil.createLinkHeader(metadataPageable.getFirstPage().getHref(), LinkUtil.REL_FIRST));
        }
        if (metadataPageable.containsLastPage()) {
            appendCommaIfNecessary(linkHeader);
            linkHeader.append(LinkUtil.createLinkHeader(metadataPageable.getLastPage().getHref(), LinkUtil.REL_LAST));
        }

        if (linkHeader.length() > 0) {
            event.getResponse().addHeader(HttpHeaders.LINK, linkHeader.toString());
        }
    }

    final void appendCommaIfNecessary(final StringBuilder linkHeader) {
        if (linkHeader.length() > 0) {
            linkHeader.append(", ");
        }
    }
}