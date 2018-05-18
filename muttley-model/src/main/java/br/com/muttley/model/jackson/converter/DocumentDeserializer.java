package br.com.muttley.model.jackson.converter;

import br.com.muttley.model.Document;
import br.com.muttley.model.jackson.converter.event.DocumentResolverEvent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 29/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class DocumentDeserializer<T extends Document> extends JsonDeserializer<T> {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
            final DocumentResolverEvent<T> event = createEventResolver(node.asText());
            //disparando para alguem ouvir esse evento
            this.eventPublisher.publishEvent(event);
            //retornando valor recuperado
            return event.isResolved() ? event.getValueResolved() : this.newInstance(event.getId());
        }
        /**provavelmente o deserializer está sendo usado fora do contexto do spring
         *ou seja, devemos apenas injetar o ID e nada mais
         */
        return node.isNull() ? null : this.newInstance(node.asText());
    }

    /**
     * Deve retornar uma instancia de {@link DocumentResolverEvent}
     *
     * @param id -> id do documento
     */
    protected abstract DocumentResolverEvent<T> createEventResolver(final String id);

    /**
     * Talvez um determinado serviço não tenha um listener para resolver essa dependencia.
     * Caso isso ocorra, simplismente devolvemos uma nova instancia com apenas o id preenchido
     */
    protected abstract T newInstance(final String id);
}
