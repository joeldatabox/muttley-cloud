package br.com.muttley.domain.service.listener;

import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.headers.components.MuttleySerializeType;
import br.com.muttley.model.ModelSync;
import br.com.muttley.model.jackson.converter.event.ModelSyncResolverEvent;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Joel Rodrigues Moreira on 10/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractModelSyncResolverEventListener<T extends ModelSync, M extends ModelSyncResolverEvent<T>> extends AbstractModelResolverEventListener<T, M> {
    @Autowired
    private MuttleySerializeType serializerType;

    public AbstractModelSyncResolverEventListener() {
        super();
    }

    @Override
    public void onApplicationEvent(final M event) {
        if (serializerType.isInternal()) {
            if (ObjectId.isValid(event.getSource())) {
                super.onApplicationEvent(event);
            } else {
                event.setValueResolved(this.loadValueBySync(event.getSource()));
            }
        } else if (serializerType.isObjectId()) {
            if (ObjectId.isValid(event.getSource())) {
                super.onApplicationEvent(event);
            } else {
                event.setValueResolved(this.loadValueBySync(event.getSource()));
            }
        } else if (serializerType.isSync()) {
            event.setValueResolved(this.loadValueBySync(event.getSource()));
        } else {
            throw new MuttleyException("deu asdfasdfasdf");
        }
    }

    protected abstract T loadValueBySync(final String sync);
}
