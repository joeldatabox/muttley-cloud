package br.com.muttley.rest.hateoas.event;

import br.com.muttley.model.Model;
import org.springframework.context.ApplicationEvent;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public class ResourceCreatedEvent extends ApplicationEvent {
    private final HttpServletResponse response;
    private final Model model;

    public ResourceCreatedEvent(final Object source, final HttpServletResponse response, final Model model) {
        super(source);
        this.response = response;
        this.model = model;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Model getModel() {
        return model;
    }
}
