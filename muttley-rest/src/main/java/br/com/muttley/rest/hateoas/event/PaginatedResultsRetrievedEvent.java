package br.com.muttley.rest.hateoas.event;

import br.com.muttley.rest.hateoas.resource.MetadataPageable;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public class PaginatedResultsRetrievedEvent<T extends Serializable> extends ApplicationEvent {
    private final UriComponentsBuilder uriBuilder;
    private final HttpServletResponse response;
    private final MetadataPageable metadataPageable;

    public PaginatedResultsRetrievedEvent(
            final Object source,
            final UriComponentsBuilder uriBuilder,
            final HttpServletResponse response,
            final MetadataPageable metadataPageable) {
        super(source);
        this.uriBuilder = uriBuilder;
        this.response = response;
        this.metadataPageable = metadataPageable;
    }

    public UriComponentsBuilder getUriBuilder() {
        return uriBuilder;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public MetadataPageable getMetadataPageable() {
        return metadataPageable;
    }

    /**
     * The object on which the Event initially occurred.
     *
     * @return The object on which the Event initially occurred.
     */
    @SuppressWarnings("unchecked")
    public final Class<T> getClazz() {
        return (Class<T>) getSource();
    }

}
