package br.com.muttley.model.jackson.converter.event;

import br.com.muttley.model.ModelSync;

/**
 * @author Joel Rodrigues Moreira on 06/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class ModelSyncResolverEvent<T extends ModelSync> extends ModelResolverEvent<T> {
    public ModelSyncResolverEvent(String syncOrId) {
        super(syncOrId);
    }

    public ModelSyncResolverEvent<T> setValueResolved(final T valueResolved) {
        super.setValueResolved(valueResolved);
        return this;
    }
}
