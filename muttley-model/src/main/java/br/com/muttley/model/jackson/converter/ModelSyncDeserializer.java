package br.com.muttley.model.jackson.converter;

import br.com.muttley.model.ModelSync;
import br.com.muttley.model.jackson.converter.event.ModelSyncResolverEvent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class ModelSyncDeserializer<T extends ModelSync> extends DocumentDeserializer<T> {

    @Override
    public T deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);
        //se alguem enviou um objeto completo devemos tentar deserializar o mesmo!
        /*if (node.isObject()) {
            return this.mapper.readValue(parser, clazz);
        }*/
        //Vamos verificar se o deserializer está no contexto do spring e que o mesmo conseguiu injetar o eventPublisher
        if (eventPublisher != null) {
            //criando evento
            final ModelSyncResolverEvent<T> event = this.createEventResolver(node.asText());
            //disparando para alguem ouvir esse evento
            this.eventPublisher.publishEvent(event);
            //retornando valor recuperado
            return event.isResolved() ? event.getValueResolved() : this.newInstance(event.getSource());
        }
        /**provavelmente o deserializer está sendo usado fora do contexto do spring
         *ou seja, devemos apenas injetar o ID e nada mais
         */
        return node.isNull() ? null : this.newInstance(node.asText());
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
}
