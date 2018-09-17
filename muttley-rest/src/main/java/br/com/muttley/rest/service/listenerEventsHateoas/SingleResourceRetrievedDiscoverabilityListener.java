package br.com.muttley.rest.service.listenerEventsHateoas;


import br.com.muttley.rest.hateoas.event.SingleResourceRetrievedEvent;
import com.google.common.base.Preconditions;
import com.google.common.net.HttpHeaders;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;

import static br.com.muttley.rest.util.LinkUtil.createLinkHeader;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
@Component
public class SingleResourceRetrievedDiscoverabilityListener implements ApplicationListener<SingleResourceRetrievedEvent> {

    @Override
    public void onApplicationEvent(final SingleResourceRetrievedEvent resourceRetrievedEvent) {

        Preconditions.checkNotNull(resourceRetrievedEvent);

        final HttpServletResponse response = resourceRetrievedEvent.getResponse();
        addLinkHeaderOnSingleResourceRetrieval(response);
    }

    void addLinkHeaderOnSingleResourceRetrieval(final HttpServletResponse response) {
        String requestURL = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri().toASCIIString();
        if (requestURL.endsWith("/")) {
            requestURL = requestURL.substring(0, requestURL.length() - 1);
        }
        final int positionOfLastSlash = requestURL.lastIndexOf("/");
        final String uriForResourceCreation = requestURL.substring(0, positionOfLastSlash);

        final String linkHeaderValue = createLinkHeader(uriForResourceCreation, "collection");
        response.addHeader(HttpHeaders.LINK, linkHeaderValue);
    }

}