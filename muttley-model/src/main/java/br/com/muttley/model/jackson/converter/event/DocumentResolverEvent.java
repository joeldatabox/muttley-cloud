package br.com.muttley.model.jackson.converter.event;

import br.com.muttley.model.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 29/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Classe que abstrai a criação de eventos para deserializar documentos
 */
@Accessors(chain = true)
public abstract class DocumentResolverEvent<T extends Document> extends ApplicationEvent {
    final String id;
    @Getter
    @Setter
    protected T valueResolved;
    @Getter
    @Setter
    private boolean resolved;

    public DocumentResolverEvent(final String id) {
        super(id);
        this.id = id;
        this.resolved = false;
    }

    public String getSource() {
        return id;
    }

}
