package br.com.muttley.model.security.jackson;

import br.com.muttley.model.security.User;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class UserDeserializer extends JsonDeserializer<User> {
    protected final ObjectMapper mapper;
    protected final ApplicationEventPublisher eventPublisher;

    public UserDeserializer(final ObjectMapper mapper, final ApplicationEventPublisher eventPublisher) {
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public User deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);
        final UserEventResolver event = new UserEventResolver(node.asText());
        //disparando para alguem ouvir esse evento
        this.eventPublisher.publishEvent(event);
        //retornando valor recuperado
        return event.isResolved() ? event.getUserResolver() : new User().setEmail(event.getEmail());
    }
}
