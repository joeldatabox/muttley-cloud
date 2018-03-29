package br.com.muttley.model.jackson.converter.event;

import br.com.muttley.model.Document;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 29/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class DocumentEventResolver<T extends Document> extends ApplicationEvent {
    final String id;
    protected T valueResolved;

    public DocumentEventResolver(final String id) {
        super(id);
        this.id = id;
    }

    public T getValueResolved() {
        return valueResolved;
    }

    public DocumentEventResolver<T> setValueResolved(final T valueResolved) {
        this.valueResolved = valueResolved;
        return this;
    }

    public String getId() {
        return id;
    }
}
