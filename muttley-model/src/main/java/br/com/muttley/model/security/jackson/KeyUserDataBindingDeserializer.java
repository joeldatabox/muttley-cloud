package br.com.muttley.model.security.jackson;

import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.model.security.KeyUserDataBinding;
import br.com.muttley.model.security.KeyUserDataBindingAvaliable;
import br.com.muttley.model.security.events.KeyUserDataBindingResolverEvent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira 10/02/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class KeyUserDataBindingDeserializer extends JsonDeserializer<KeyUserDataBinding> {
    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    @Override
    public KeyUserDataBinding deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final JsonNode node = parser.getCodec().readTree(parser);
        if (node.isNull()) {
            return null;
        }
        if (eventPublisher == null) {
            final KeyUserDataBinding result = KeyUserDataBindingAvaliable.from(node.asText());
            if (result == null) {
                throw new MuttleyException("Não foi possivel resolver por falta do publisher e falta no cache");
            }
            return result;
        }
        final KeyUserDataBindingResolverEvent event = new KeyUserDataBindingResolverEvent(node.asText());
        eventPublisher.publishEvent(event);
        return event.getResolvedValue();
    }
}
