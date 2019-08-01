package br.com.muttley.model.security.jackson;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.UserResolverEvent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    @Override
    public User deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);
        //verificando se o eventPublisher foi injetado no contexto do spring
        if (this.eventPublisher != null) {
            final UserResolverEvent event = new UserResolverEvent(node.asText());
            //disparando para alguem ouvir esse evento
            this.eventPublisher.publishEvent(event);
            //retornando valor recuperado
            return event.isResolved() ? event.getUserResolver() : new User().setUserName(event.getUserName());
        }
        //deserializando o usuário com apenas o username mesmo
        return node.isNull() ? null : new User().setUserName(node.asText());
    }
}
