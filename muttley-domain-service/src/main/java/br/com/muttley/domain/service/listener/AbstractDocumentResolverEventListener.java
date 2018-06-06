package br.com.muttley.domain.service.listener;

import br.com.muttley.model.Document;
import br.com.muttley.model.jackson.converter.event.DocumentResolverEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author Joel Rodrigues Moreira on 25/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Classe abstrata que implementa a regra de negocio para se resolver
 */
public abstract class AbstractDocumentResolverEventListener<T extends Document, D extends DocumentResolverEvent<T>> implements ApplicationListener<D> {

    @Override
    public void onApplicationEvent(final D event) {
        event.setValueResolved(this.loadValueById(event.getSource()));
    }

    /**
     * Deve retorna uma instancia de um documento
     */
    protected abstract T loadValueById(final String id);
}
