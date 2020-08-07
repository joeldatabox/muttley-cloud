package br.com.muttley.exception.service.event;

import br.com.muttley.exception.ErrorMessage;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 06/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MuttleyExceptionEvent<T extends ErrorMessage> extends ApplicationEvent {
    private T source;

    public MuttleyExceptionEvent() {
        super("null");
    }

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public MuttleyExceptionEvent(final T source) {
        super(source);
        this.source = source;
    }

    @Override
    public T getSource() {
        return source;
    }

    public MuttleyExceptionEvent setSource(final T source) {
        this.source = source;
        return this;
    }
}
