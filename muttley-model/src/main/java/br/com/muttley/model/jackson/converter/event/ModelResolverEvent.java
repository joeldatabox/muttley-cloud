package br.com.muttley.model.jackson.converter.event;

import br.com.muttley.model.MultiTenancyModel;

/**
 * @author Joel Rodrigues Moreira on 25/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class ModelResolverEvent<T extends MultiTenancyModel> extends DocumentResolverEvent<T> {
    public ModelResolverEvent(final String id) {
        super(id);
    }
}
