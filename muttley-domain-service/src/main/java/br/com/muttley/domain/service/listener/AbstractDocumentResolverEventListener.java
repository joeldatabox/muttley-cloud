package br.com.muttley.domain.service.listener;

import br.com.muttley.domain.service.Service;
import br.com.muttley.model.Document;
import br.com.muttley.model.jackson.converter.event.DocumentResolverEvent;
import br.com.muttley.model.security.User;
import org.bson.types.ObjectId;
import org.springframework.context.ApplicationListener;

/**
 * @author Joel Rodrigues Moreira on 25/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Classe abstrata que implementa a regra de negocio para se resolver
 */
public abstract class AbstractDocumentResolverEventListener<T extends Document<?>, D extends DocumentResolverEvent<T>> implements ApplicationListener<D> {
    protected final Service<T, ObjectId> service;

    public AbstractDocumentResolverEventListener(final Service<T, ObjectId> service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(final D event) {
        event.setValueResolved(service.findById(getCurrentUser(), new ObjectId(event.getSource())));
    }

    /**
     * Deve retorna o usuário corrente da requisição
     */
    protected abstract User getCurrentUser();
}
