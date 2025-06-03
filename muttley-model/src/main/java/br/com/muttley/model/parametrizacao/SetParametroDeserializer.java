package br.com.muttley.model.parametrizacao;

import br.com.muttley.model.jackson.converter.CollectionModelSyncDeserializer;
import br.com.muttley.model.jackson.converter.event.ModelSyncResolverEvent;
import br.com.muttley.model.security.events.ParametroResolverEvent;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Carolina Cedro on 14/05/2025.
 * e-mail: <a href="mailto:ana.carolina@maxxsoft.com">ana.carolina@maxxsoft.com</a>
 * @project agrifocus-cloud
 */
@Component
public class SetParametroDeserializer extends CollectionModelSyncDeserializer<Parametro, Set<Parametro>> {

    @Override
    protected ModelSyncResolverEvent<Parametro> createEventResolver(final String idOrSync) {
        return new ParametroResolverEvent(idOrSync);
    }

    @Override
    protected Parametro newInstance(final String idOrSync) {
        return ObjectId.isValid(idOrSync) ? new Parametro().setId(idOrSync) : new Parametro().setSync(idOrSync);
    }

    @Override
    protected Set<Parametro> cast(final List<Parametro> itens) {
        return new HashSet<>(itens);
    }
}

