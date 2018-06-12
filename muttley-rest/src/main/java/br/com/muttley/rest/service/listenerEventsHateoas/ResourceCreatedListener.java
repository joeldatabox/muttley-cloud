package br.com.muttley.rest.service.listenerEventsHateoas;


import br.com.muttley.rest.hateoas.event.ResourceCreatedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
@Component
public class ResourceCreatedListener implements ApplicationListener<ResourceCreatedEvent> {
    @Override
    public void onApplicationEvent(final ResourceCreatedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event has null");
        }
        event.getResponse()
                .setHeader(HttpHeaders.LOCATION, ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(event.getSource().getId())
                        .toUri().toASCIIString());
    }
}
