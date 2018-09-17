package br.com.muttley.rest.hateoas.event;

import org.springframework.context.ApplicationEvent;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public class SingleResourceRetrievedEvent extends ApplicationEvent {
    private final HttpServletResponse response;

    public SingleResourceRetrievedEvent(final HttpServletResponse response) {
        super(response);
        this.response = response;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

}