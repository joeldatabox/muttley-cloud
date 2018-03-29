package br.com.muttley.model.jackson.converter;

import br.com.muttley.model.Document;
import br.com.muttley.model.jackson.converter.event.DocumentEventResolver;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 29/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class DocumentDeserializer<T extends Document> extends JsonDeserializer<T> {
    protected final Class<T> clazz;
    protected final ObjectMapper mapper;
    protected final ApplicationEventPublisher eventPublisher;

    public DocumentDeserializer(final Class<T> clazz, final ObjectMapper mapper, final ApplicationEventPublisher eventPublisher) {
        this.clazz = clazz;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public T deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);
        //se alguem enviou um objeto completo devemos tentar deserializar o mesmo!
        /*if (node.isObject()) {
            return this.mapper.readValue(parser, clazz);
        }*/
        //criando evento
        final DocumentEventResolver<T> event = createEventResolver(node.asText());
        //disparando para alguem ouvir esse evento
        this.eventPublisher.publishEvent(event);
        //retornando valor recuperado
        return event.getValueResolved();
    }

    /**
     * Deve retornar uma instancia de {@link DocumentEventResolver}
     *
     * @param id -> id do documento
     */
    protected abstract DocumentEventResolver<T> createEventResolver(final String id);
}
