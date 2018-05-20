package br.com.muttley.model.jackson.converter.event;

import br.com.muttley.model.Document;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 29/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class DocumentResolverEvent<T extends Document> extends ApplicationEvent {
    final String id;
    protected T valueResolved;

    public DocumentResolverEvent(final String id) {
        super(id);
        this.id = id;
    }

    public T getValueResolved() {
        return valueResolved;
    }

    public DocumentResolverEvent<T> setValueResolved(final T valueResolved) {
        this.valueResolved = valueResolved;
        return this;
    }

    public String getSource() {
        return id;
    }

    public boolean isResolved() {
        return this.valueResolved != null;
    }
}
