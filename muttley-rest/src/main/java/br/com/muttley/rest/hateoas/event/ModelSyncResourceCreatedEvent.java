package br.com.muttley.rest.hateoas.event;

import br.com.muttley.model.ModelSync;
import org.springframework.context.ApplicationEvent;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Joel Rodrigues Moreira on 06/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ModelSyncResourceCreatedEvent extends ApplicationEvent {
    private final ModelSync model;
    private final HttpServletResponse response;

    public ModelSyncResourceCreatedEvent(final ModelSync source, final HttpServletResponse response) {
        super(source);
        this.model = source;
        this.response = response;
    }

    @Override
    public ModelSync getSource() {
        return this.model;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}
