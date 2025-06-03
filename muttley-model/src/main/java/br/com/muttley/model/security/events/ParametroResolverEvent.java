package br.com.muttley.model.security.events;

import br.com.muttley.model.jackson.converter.event.ModelSyncResolverEvent;
import br.com.muttley.model.parametrizacao.Parametro;

public class ParametroResolverEvent extends ModelSyncResolverEvent<Parametro> {
    public ParametroResolverEvent(final String syncOrId) {
        super(syncOrId);
    }
}
