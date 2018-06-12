package br.com.muttley.model.jackson.converter.event;

import br.com.muttley.model.Document;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 29/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Classe que abstrai a criação de eventos para deserializar documentos
 */
public abstract class DocumentResolverEvent<T extends Document> extends ApplicationEvent {
    final String id;
    protected T valueResolved;
    private boolean resolved;

    public DocumentResolverEvent(final String id) {
        super(id);
        this.id = id;
        this.resolved = false;
    }

    public T getValueResolved() {
        return valueResolved;
    }

    public DocumentResolverEvent<T> setValueResolved(final T valueResolved) {
        this.valueResolved = valueResolved;
        this.resolved = true;
        return this;
    }

    public String getSource() {
        return id;
    }

    public boolean isResolved() {
        return resolved;
    }
}
