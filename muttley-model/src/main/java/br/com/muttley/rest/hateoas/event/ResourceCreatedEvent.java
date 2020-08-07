package br.com.muttley.rest.hateoas.event;

import br.com.muttley.model.Document;
import org.springframework.context.ApplicationEvent;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public class ResourceCreatedEvent extends ApplicationEvent {
    private final HttpServletResponse response;
    private final Document model;

    public ResourceCreatedEvent(final Document source, final HttpServletResponse response) {
        super(source);
        this.response = response;
        this.model = source;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    @Override
    public Document getSource() {
        return model;
    }
}
