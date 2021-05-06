package br.com.muttley.model.jackson.converter;

import br.com.muttley.model.ModelSync;
import br.com.muttley.model.jackson.converter.event.ModelSyncResolverEvent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Joel Rodrigues Moreira on 06/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class CollectionModelSyncDeserializer<T extends ModelSync, C extends Collection<T>> extends JsonDeserializer<C> {
    @Autowired
    protected ApplicationEventPublisher eventPublisher;


    @Override
    public C deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);

        final List<String> idsOrSync = node.traverse(parser.getCodec()).readValueAs(new TypeReference<List<String>>() {
        });

        final List<T> deserializedItens = new ArrayList<>();
        //Vamos verificar se o deserializer está no contexto do spring e que o mesmo conseguiu injetar o eventPublisher
        if (eventPublisher != null) {

            for (final String idOrSync : idsOrSync) {
                //criando evento
                final ModelSyncResolverEvent<T> event = this.createEventResolver(idOrSync);
                //disparando para alguem ouvir esse evento
                this.eventPublisher.publishEvent(event);
                //adicionando valor recuperado
                deserializedItens.add(event.isResolved() ? event.getValueResolved() : this.newInstance(event.getSource()));
            }
            return cast(deserializedItens);
        } else {
            /**provavelmente o deserializer está sendo usado fora do contexto do spring
             *ou seja, devemos apenas injetar o ID e nada mais
             */
            return node.isNull() ? null : cast(idsOrSync.stream().map(it -> newInstance(it)).collect(toList()));
        }
    }

    /**
     * Deve retornar uma instancia de {@link ModelSyncResolverEvent}
     *
     * @param idOrSync -> id ou sync do documento
     */
    protected abstract ModelSyncResolverEvent<T> createEventResolver(final String idOrSync);

    /**
     * Talvez um determinado serviço não tenha um listener para resolver essa dependencia.
     * Caso isso ocorra, simplismente devolvemos uma nova instancia com apenas o id ou sync preenchido
     */
    protected abstract T newInstance(final String idOrSync);

    protected abstract C cast(final List<T> itens);
}

