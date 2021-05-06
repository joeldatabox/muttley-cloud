package br.com.muttley.rest.service.listenerEventsHateoas;

import br.com.muttley.headers.components.MuttleySerializeType;
import br.com.muttley.rest.hateoas.event.ModelSyncResourceCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author Joel Rodrigues Moreira on 06/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ModelSyncResourceCreatedEventListener implements ApplicationListener<ModelSyncResourceCreatedEvent> {

    @Autowired
    private MuttleySerializeType serializerType;

    @Override
    public void onApplicationEvent(ModelSyncResourceCreatedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event has null");
        } else if (serializerType.isObjectId()) {
            event.getResponse()
                    .setHeader(HttpHeaders.LOCATION, ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(event.getSource().getId())
                            .toUri().toASCIIString());
        } else {
            event.getResponse()
                    .setHeader(
                            HttpHeaders.LOCATION,
                            ServletUriComponentsBuilder
                                    .fromCurrentRequest()
                                    .path("/sync/{sync}")
                                    .buildAndExpand(event.getSource().getSync())
                                    .toUri()
                                    .toASCIIString()
                    );
        }
    }
}
