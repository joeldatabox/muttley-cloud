package br.com.muttley.domain.service.listener;

import br.com.muttley.domain.service.Service;
import br.com.muttley.model.Model;
import br.com.muttley.model.jackson.converter.event.ModelResolverEvent;
import org.bson.types.ObjectId;

/**
 * @author Joel Rodrigues Moreira on 25/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractModelResolverEventListener<T extends Model<?>, D extends ModelResolverEvent<T>> extends AbstractDocumentResolverEventListener<T, D> {

    public AbstractModelResolverEventListener(final Service<T, ObjectId> service) {
        super(service);
    }
}
